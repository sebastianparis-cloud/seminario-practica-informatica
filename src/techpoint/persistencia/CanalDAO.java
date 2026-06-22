package techpoint.persistencia;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.CanalVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * CanalDAO - Data Access Object para la tabla canal_venta.
 *
 * Implementa el ABM completo de canales de venta con persistencia
 * directa en MySQL via JDBC. Incorporado en el TP4 como parte
 * de la persistencia completa del sistema.
 *
 * Trazabilidad: CU005 - Gestionar Canales de Venta (nuevo en TP4).
 */
public class CanalDAO {

    // Alta: insertar nuevo canal en MySQL
    public void insertarCanal(String nombre, double comisionPct) throws ProductoExcepcion {
        String sql = "INSERT INTO canal_venta (nombre, comision_pct, activo) VALUES (?, ?, TRUE)";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, comisionPct);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al insertar canal en MySQL: " + e.getMessage());
        }
    }

    // Modificacion: actualizar comision de un canal existente
    public void actualizarComision(int idCanal, double nuevaComision) throws ProductoExcepcion {
        String sql = "UPDATE canal_venta SET comision_pct = ? WHERE id_canal = ? AND activo = TRUE";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, nuevaComision);
            ps.setInt(2, idCanal);
            int filas = ps.executeUpdate();
            ps.close();
            if (filas == 0)
                throw new ProductoExcepcion("No se encontro canal activo con ID: " + idCanal);
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al actualizar canal en MySQL: " + e.getMessage());
        }
    }

    // Baja logica: marcar canal como inactivo
    public void darDeBajaCanal(int idCanal) throws ProductoExcepcion {
        String sql = "UPDATE canal_venta SET activo = FALSE WHERE id_canal = ?";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCanal);
            int filas = ps.executeUpdate();
            ps.close();
            if (filas == 0)
                throw new ProductoExcepcion("No se encontro canal con ID: " + idCanal);
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al dar de baja canal en MySQL: " + e.getMessage());
        }
    }

    // Lectura: cargar todos los canales activos
    public ArrayList<CanalVenta> cargarCanales() throws ProductoExcepcion {
        ArrayList<CanalVenta> lista = new ArrayList<>();
        String sql = "SELECT id_canal, nombre, comision_pct FROM canal_venta WHERE activo = TRUE ORDER BY id_canal";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new CanalVenta(
                    rs.getInt("id_canal"),
                    rs.getString("nombre"),
                    rs.getDouble("comision_pct")
                ));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al cargar canales desde MySQL: " + e.getMessage());
        }
        return lista;
    }

    // Verificacion de existencia por ID
    public boolean existeCanal(int idCanal) throws ProductoExcepcion {
        String sql = "SELECT COUNT(*) FROM canal_venta WHERE id_canal = ? AND activo = TRUE";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idCanal);
            ResultSet rs = ps.executeQuery();
            rs.next();
            boolean existe = rs.getInt(1) > 0;
            rs.close();
            ps.close();
            return existe;
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al verificar canal en MySQL: " + e.getMessage());
        }
    }
}
