package techpoint.persistencia;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.Accesorio;
import techpoint.modelo.CanalVenta;
import techpoint.modelo.Consola;
import techpoint.modelo.ProductoTecnologico;
import techpoint.modelo.Smartphone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * ProductoDAO - Data Access Object para la base de datos techpoint.
 * Implementa persistencia JDBC para los cuatro casos de uso del sistema.
 * En TP4 se integra completamente con el patron MVC.
 * Trazabilidad: CU001, CU002, CU003, CU004.
 */
public class ProductoDAO {

    // CU003 - Leer productos desde MySQL
    public ArrayList<ProductoTecnologico> cargarProductos() throws ProductoExcepcion {
        ArrayList<ProductoTecnologico> lista = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.costo_usd, p.costo_ars, "
                   + "p.margen_seguridad, p.activo, c.nombre AS categoria "
                   + "FROM producto p "
                   + "JOIN categoria c ON p.id_categoria = c.id_categoria "
                   + "ORDER BY p.id_producto";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id           = rs.getInt("id_producto");
                String nombre    = rs.getString("nombre");
                double costoUSD  = rs.getDouble("costo_usd");
                double costoARS  = rs.getDouble("costo_ars");
                double margen    = rs.getDouble("margen_seguridad");
                boolean activo   = rs.getBoolean("activo");
                String categoria = rs.getString("categoria");
                ProductoTecnologico p;
                if (categoria.equalsIgnoreCase("Mobile y Wearables")) {
                    p = new Smartphone(id, nombre, costoUSD, margen, "Importado", 0);
                } else if (categoria.equalsIgnoreCase("Gaming")) {
                    p = new Consola(id, nombre, costoUSD, margen, "Importado", "Ultima generacion");
                } else {
                    p = new Accesorio(id, nombre, costoUSD, margen, categoria, nombre);
                }
                p.setCostoARS(costoARS);
                if (!activo) p.darDeBaja();
                lista.add(p);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al cargar productos desde MySQL: " + e.getMessage());
        }
        return lista;
    }

    // CU004 - Leer canales desde MySQL
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

    // CU003 - Insertar producto en MySQL
    public void insertarProducto(String nombre, double costoUSD, double margen, int idCategoria)
            throws ProductoExcepcion {
        String sql = "INSERT INTO producto (nombre, costo_usd, margen_seguridad, activo, id_categoria) "
                   + "VALUES (?, ?, ?, TRUE, ?)";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, costoUSD);
            ps.setDouble(3, margen);
            ps.setInt(4, idCategoria);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al insertar producto en MySQL: " + e.getMessage());
        }
    }

    /**
     * Inserta producto con nombre descriptivo completo para el TP4.
     * Usa PreparedStatement para prevenir SQL Injection.
     */
    public void insertarProductoCompleto(String nombre, String descripcion,
                                          double costoUSD, double costoARS,
                                          double margen, int idCategoria)
            throws ProductoExcepcion {
        String sql = "INSERT INTO producto (nombre, descripcion, costo_usd, costo_ars, " +
                     "margen_seguridad, activo, id_categoria) VALUES (?, ?, ?, ?, ?, TRUE, ?)";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, descripcion != null ? descripcion : "");
            ps.setDouble(3, costoUSD);
            ps.setDouble(4, costoARS);
            ps.setDouble(5, margen);
            ps.setInt(6, idCategoria);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al insertar producto completo: " + e.getMessage());
        }
    }

    // CU003 - Baja logica en MySQL
    public void darDeBajaProducto(int idProducto) throws ProductoExcepcion {
        String sql = "UPDATE producto SET activo = FALSE WHERE id_producto = ?";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idProducto);
            int filas = ps.executeUpdate();
            ps.close();
            if (filas == 0)
                throw new ProductoExcepcion("No se encontro producto con ID " + idProducto + " en MySQL.");
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al dar de baja en MySQL: " + e.getMessage());
        }
    }

    // CU001 - Registrar cotizacion en tipo_cambio
    public int registrarCotizacion(double valorUSD) throws ProductoExcepcion {
        String sql = "INSERT INTO tipo_cambio (valor_usd, fecha_hora, fuente, validado) VALUES (?, ?, 'Sistema TechPoint', TRUE)";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, valorUSD);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : 0;
            keys.close();
            ps.close();
            return id;
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al registrar cotizacion en MySQL: " + e.getMessage());
        }
    }

    // CU002/CU004 - Registrar calculo en margen_rentabilidad
    public void registrarMargen(int idProducto, int idCotizacion, int idCanal,
                                 double precioSugerido, double gananciaNeta, boolean alerta)
            throws ProductoExcepcion {
        String sql = "INSERT INTO margen_rentabilidad "
                   + "(precio_sugerido_ars, ganancia_real, alerta_activa, fecha_calculo, id_producto, id_cotizacion, id_canal) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionMySQL.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, precioSugerido);
            ps.setDouble(2, gananciaNeta);
            ps.setBoolean(3, alerta);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, idProducto);
            ps.setInt(6, idCotizacion);
            ps.setInt(7, idCanal);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new ProductoExcepcion("Error al registrar margen en MySQL: " + e.getMessage());
        }
    }

    // Verificacion de conexion al iniciar
    public boolean verificarConexion() {
        try {
            ConexionMySQL.obtenerConexion();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
