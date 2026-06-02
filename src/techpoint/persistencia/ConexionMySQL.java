package techpoint.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConexionMySQL - gestiona la conexion JDBC con la base de datos techpoint.
 * Patron Singleton: una unica conexion reutilizada durante toda la ejecucion.
 * Protocolo: JDBC / TCP-IP | Puerto: 3306 | Driver: MySQL Connector/J 8.0
 * Trazabilidad: Definiciones de Comunicacion TP2 (seccion 15).
 */
public class ConexionMySQL {

    private static final String URL     = "jdbc:mysql://localhost:3306/techpoint?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root";

    private static Connection conexion = null;

    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "Driver MySQL no encontrado. Verificá que el JAR está en lib\\", e);
            }
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("  Conexion MySQL cerrada correctamente.");
                }
            } catch (SQLException e) {
                System.out.println("  [ADVERTENCIA] Error al cerrar conexion: " + e.getMessage());
            }
        }
    }

    public static boolean estaConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
