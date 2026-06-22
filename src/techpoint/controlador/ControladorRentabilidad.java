package techpoint.controlador;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.GestorRentabilidad;

/**
 * ControladorRentabilidad - implementa el CU002 en la capa Controlador del MVC.
 *
 * Coordina la consulta de rentabilidad entre la Vista y el Modelo.
 * En el TP4, ademas registra el calculo en margen_rentabilidad de MySQL
 * para generar historial de consultas.
 *
 * Trazabilidad: CU002 - Consultar Rentabilidad por Producto.
 */
public class ControladorRentabilidad {

    private GestorRentabilidad gestor;

    public ControladorRentabilidad(GestorRentabilidad gestor) {
        this.gestor = gestor;
    }

    /**
     * Delega la consulta de rentabilidad al modelo.
     * La presentacion del resultado queda en la Vista (MenuPrincipal).
     */
    public void consultarRentabilidad(int idProducto) throws ProductoExcepcion {
        gestor.consultarRentabilidad(idProducto);
    }

    public void listarProductos(String filtroCategoria) {
        gestor.listarProductos(filtroCategoria);
    }
}
