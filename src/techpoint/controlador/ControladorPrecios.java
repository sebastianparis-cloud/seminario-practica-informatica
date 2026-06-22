package techpoint.controlador;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.GestorRentabilidad;
import techpoint.persistencia.ConexionMySQL;
import techpoint.persistencia.ProductoDAO;
import techpoint.servicio.CotizacionManual;
import techpoint.servicio.CotizacionOnline;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ControladorPrecios - implementa el CU001 en la capa Controlador del MVC.
 *
 * Recibe la solicitud de la Vista (MenuPrincipal), coordina con el Modelo
 * (GestorRentabilidad) y con la capa de persistencia (ProductoDAO).
 *
 * Patron MVC: este controlador separa la logica de CU001 del menu de consola,
 * permitiendo reemplazar la vista sin modificar la logica de negocio.
 *
 * Trazabilidad: CU001 - Actualizar Estrategia de Precios.
 */
public class ControladorPrecios {

    private GestorRentabilidad gestor;
    private ProductoDAO productoDAO;

    public ControladorPrecios(GestorRentabilidad gestor) {
        this.gestor = gestor;
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Sincroniza la cotizacion y recalcula todos los precios.
     * Registra la cotizacion en MySQL si hay conexion activa.
     */
    public String sincronizarPrecios() {
        try {
            gestor.sincronizarEstrategiaDePrecios();
            if (ConexionMySQL.estaConectado()) {
                productoDAO.registrarCotizacion(gestor.getCotizacionCache());
            }
            return "OK: Precios actualizados. Cotizacion: $"
                + String.format("%.2f", gestor.getCotizacionCache())
                + " | Productos: " + contarProductosActivos();
        } catch (ProductoExcepcion e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Actualiza la cotizacion manualmente y recalcula precios.
     * Flujo alternativo S5 del CU001.
     */
    public String actualizarCotizacionManual(double nuevaCotizacion) {
        try {
            CotizacionManual cm = (CotizacionManual) gestor.getServicioCotizacion();
            cm.actualizarCotizacion(nuevaCotizacion);
            gestor.actualizarCotizacionCache(nuevaCotizacion);
            return sincronizarPrecios();
        } catch (ProductoExcepcion e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String sincronizarPreciosOnline() {
        try {
            CotizacionOnline cotizacionOnline = new CotizacionOnline();
            double valorOnline = cotizacionOnline.obtenerCotizacion();
            CotizacionManual cm = (CotizacionManual) gestor.getServicioCotizacion();
            cm.actualizarCotizacion(valorOnline);
            gestor.actualizarCotizacionCache(valorOnline);
            gestor.sincronizarEstrategiaDePrecios();
            if (ConexionMySQL.estaConectado()) {
                productoDAO.registrarCotizacion(valorOnline);
            }
            return "OK: Cotizacion online: $"
                + String.format("%.2f", valorOnline)
                + " (Dolar Blue) | Precios actualizados.";
        } catch (ProductoExcepcion e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Flujo automatico: intenta cotizacion online; si falla, pide valor manual.
     * En ambos casos recalcula precios y retorna resumen.
     */
    public String sincronizarPreciosAutomatico(Scanner scanner) {
        System.out.println("  Actualizando cotizacion...");
        double valor = 0;
        boolean usandoOnline = false;

        // Intentar online
        try {
            CotizacionOnline cotizacionOnline = new CotizacionOnline();
            valor = cotizacionOnline.obtenerCotizacion();
            usandoOnline = true;
            System.out.println("  Cotizacion online: $"
                + String.format("%.2f", valor) + " (Dolar Blue)");
        } catch (ProductoExcepcion e) {
            System.out.println("  Sin conexion a la API. " + e.getMessage());
            System.out.print("  Ingrese la cotizacion USD/ARS manualmente: $");
            while (valor <= 0) {
                try {
                    valor = scanner.nextDouble(); scanner.nextLine();
                    if (valor <= 0) {
                        System.out.print("  El valor debe ser mayor a cero. Ingrese nuevamente: $");
                    }
                } catch (InputMismatchException ex) {
                    System.out.print("  [ERROR] Ingrese un numero valido: $");
                    scanner.nextLine();
                }
            }
        }

        // Aplicar valor al gestor y recalcular
        try {
            CotizacionManual cm = (CotizacionManual) gestor.getServicioCotizacion();
            cm.actualizarCotizacion(valor);
            gestor.actualizarCotizacionCache(valor);
            gestor.sincronizarEstrategiaDePrecios();
            if (ConexionMySQL.estaConectado()) {
                productoDAO.registrarCotizacion(valor);
            }
        } catch (ProductoExcepcion e) {
            return "ERROR al recalcular precios: " + e.getMessage();
        }

        return "Precios actualizados para " + contarProductosActivos() + " productos.";
    }

    private int contarProductosActivos() {
        int count = 0;
        for (techpoint.modelo.ProductoTecnologico p : gestor.getCatalogo()) {
            if (p.isActivo()) count++;
        }
        return count;
    }
}
