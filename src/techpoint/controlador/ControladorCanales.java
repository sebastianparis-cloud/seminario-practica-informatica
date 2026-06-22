package techpoint.controlador;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.CanalVenta;
import techpoint.modelo.GestorRentabilidad;
import techpoint.persistencia.CanalDAO;
import techpoint.persistencia.ConexionMySQL;

import java.util.ArrayList;

/**
 * ControladorCanales - nuevo en TP4. Implementa el CU005 en la capa Controlador del MVC.
 *
 * Gestiona el ABM completo de canales de venta: alta, modificacion de comision
 * y baja logica. Cada operacion sincroniza el ArrayList en memoria con MySQL.
 *
 * Trazabilidad: CU005 - Gestionar Canales de Venta (nuevo en TP4).
 * Resuelve el pendiente del TP3 donde las comisiones estaban hardcodeadas.
 */
public class ControladorCanales {

    private GestorRentabilidad gestor;
    private CanalDAO canalDAO;

    public ControladorCanales(GestorRentabilidad gestor) {
        this.gestor = gestor;
        this.canalDAO = new CanalDAO();
    }

    /**
     * Da de alta un nuevo canal: lo agrega al ArrayList y persiste en MySQL.
     */
    public String altaCanal(String nombre, double comisionPct) throws ProductoExcepcion {
        // ID temporal para objeto en memoria; MySQL asigna el ID real con AUTO_INCREMENT
        int nuevoId = 1;
        for (CanalVenta c : gestor.getCanales()) {
            if (c.getId() >= nuevoId) nuevoId = c.getId() + 1;
        }

        CanalVenta nuevo = new CanalVenta(nuevoId, nombre, comisionPct);
        gestor.agregarCanal(nuevo);

        if (ConexionMySQL.estaConectado()) {
            canalDAO.insertarCanal(nombre, comisionPct);
        }

        return "OK: Canal '" + nombre + "' registrado con " + comisionPct + "% de comision.";
    }

    /**
     * Modifica la comision de un canal: actualiza en memoria y en MySQL.
     */
    public String modificarComision(int idCanal, double nuevaComision) throws ProductoExcepcion {
        CanalVenta canal = buscarCanalPorId(idCanal);
        if (canal == null)
            throw new ProductoExcepcion("No se encontro canal con ID: " + idCanal);

        if (nuevaComision < 0 || nuevaComision >= 100)
            throw new ProductoExcepcion("La comision debe estar entre 0% y 100%.");

        canal.setComisionPct(nuevaComision);

        if (ConexionMySQL.estaConectado()) {
            canalDAO.actualizarComision(idCanal, nuevaComision);
        }

        return "OK: Comision de '" + canal.getNombre() + "' actualizada a " + nuevaComision + "%.";
    }

    /**
     * Da de baja logica un canal: lo marca inactivo en memoria y en MySQL.
     */
    public String bajaCanal(int idCanal) throws ProductoExcepcion {
        CanalVenta canal = buscarCanalPorId(idCanal);
        if (canal == null)
            throw new ProductoExcepcion("No se encontro canal con ID: " + idCanal);
        if (!canal.isActivo())
            throw new ProductoExcepcion("El canal '" + canal.getNombre() + "' ya esta inactivo.");

        canal.darDeBaja();

        if (ConexionMySQL.estaConectado()) {
            canalDAO.darDeBajaCanal(idCanal);
        }

        return "OK: Canal '" + canal.getNombre() + "' dado de baja.";
    }

    /**
     * Lista todos los canales (activos e inactivos) del ArrayList.
     */
    public void listarCanales() {
        System.out.println();
        ArrayList<CanalVenta> canales = gestor.getCanales();
        if (canales.isEmpty()) {
            System.out.println("  No hay canales registrados.");
            return;
        }
        System.out.printf("  %-5s %-20s %-12s %s%n", "ID", "CANAL", "COMISION", "ESTADO");
        System.out.println("  " + "-".repeat(50));
        for (CanalVenta c : canales) {
            System.out.printf("  %-5d %-20s %-12s %s%n",
                c.getId(), c.getNombre(),
                String.format("%.1f%%", c.getComisionPct()),
                c.isActivo() ? "ACTIVO" : "INACTIVO");
        }
    }

    public void analizarPorCanal(int idProducto) throws ProductoExcepcion {
        gestor.analizarPorCanal(idProducto);
    }

    private CanalVenta buscarCanalPorId(int id) {
        for (CanalVenta c : gestor.getCanales()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

}
