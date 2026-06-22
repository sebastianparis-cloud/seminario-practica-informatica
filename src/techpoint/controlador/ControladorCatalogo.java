package techpoint.controlador;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.Accesorio;
import techpoint.modelo.Consola;
import techpoint.modelo.GestorRentabilidad;
import techpoint.modelo.Notebook;
import techpoint.modelo.ProductoTecnologico;
import techpoint.modelo.Smartphone;
import techpoint.persistencia.ConexionMySQL;
import techpoint.persistencia.ProductoDAO;

import java.util.ArrayList;

/**
 * ControladorCatalogo - implementa el CU003 en la capa Controlador del MVC.
 *
 * Coordina las operaciones de ABM del catalogo entre la Vista y el Modelo,
 * garantizando que cada operacion persiste en MySQL cuando hay conexion.
 *
 * Uso de arreglo nativo: el metodo obtenerCategorias() retorna un String[]
 * con las categorias disponibles — complementa el uso de ArrayList del modelo.
 *
 * Trazabilidad: CU003 - Gestionar Catalogo Tecnologico.
 */
public class ControladorCatalogo {

    private GestorRentabilidad gestor;
    private ProductoDAO productoDAO;

    // ARREGLO NATIVO: categorias validas del sistema (complementa ArrayList)
    private static final String[] CATEGORIAS = {
        "Mobile y Wearables",
        "Gaming",
        "Audio",
        "Hogar y Tecnologia",
        "Computacion"
    };

    // ARREGLO NATIVO: IDs de categoria en MySQL correspondientes a CATEGORIAS[]
    private static final int[] IDS_CATEGORIA = { 1, 2, 3, 4, 5 };

    public ControladorCatalogo(GestorRentabilidad gestor) {
        this.gestor = gestor;
        this.productoDAO = new ProductoDAO();
    }

    /**
     * Retorna el arreglo de categorias disponibles.
     * Uso explicito de arreglo nativo (String[]) como complemento al ArrayList.
     */
    public String[] obtenerCategorias() {
        return CATEGORIAS;
    }

    /**
     * Retorna el ID de categoria en MySQL dado el indice del arreglo CATEGORIAS.
     */
    public int obtenerIdCategoria(int indice) {
        if (indice >= 0 && indice < IDS_CATEGORIA.length) {
            return IDS_CATEGORIA[indice];
        }
        return -1;
    }

    /**
     * Da de alta un nuevo producto: lo agrega al ArrayList en memoria
     * y lo persiste en MySQL si hay conexion activa.
     */
    public String altaProducto(String nombre, double costoUSD, double margen,
                                int tipoProducto, String param1, String param2)
            throws ProductoExcepcion {

        // ID temporal para objeto en memoria; MySQL asigna el ID real con AUTO_INCREMENT
        int nuevoId = 1;
        for (ProductoTecnologico p : gestor.getCatalogo()) {
            if (p.getId() >= nuevoId) nuevoId = p.getId() + 1;
        }

        ProductoTecnologico nuevo;
        int idCategoria;

        switch (tipoProducto) {
            case 1:
                int gb = 0;
                try { gb = Integer.parseInt(param2); } catch (NumberFormatException e) { gb = 0; }
                nuevo = new Smartphone(nuevoId, nombre, costoUSD, margen, param1, gb);
                idCategoria = IDS_CATEGORIA[0];
                break;
            case 2:
                nuevo = new Consola(nuevoId, nombre, costoUSD, margen, param1, param2);
                idCategoria = IDS_CATEGORIA[1];
                break;
            case 3:
                nuevo = new Accesorio(nuevoId, nombre, costoUSD, margen, "Audio", param1);
                idCategoria = IDS_CATEGORIA[2];
                break;
            case 4:
                nuevo = new Accesorio(nuevoId, nombre, costoUSD, margen, "Hogar y Tecnologia", param1);
                idCategoria = IDS_CATEGORIA[3];
                break;
            case 5:
                int ram = 0;
                try { ram = Integer.parseInt(param2); } catch (NumberFormatException e) { ram = 8; }
                nuevo = new Notebook(nuevoId, nombre, costoUSD, margen, param1, ram);
                idCategoria = IDS_CATEGORIA[4];
                break;
            default:
                throw new ProductoExcepcion("Tipo de producto invalido: " + tipoProducto);
        }

        gestor.agregarProducto(nuevo);

        if (ConexionMySQL.estaConectado()) {
            productoDAO.insertarProducto(nombre, costoUSD, margen, idCategoria);
        }

        return "OK: Producto '" + nombre + "' registrado correctamente.";
    }

    /**
     * Da de baja logica un producto: lo marca inactivo en memoria y en MySQL.
     */
    public String bajaProducto(int idProducto) throws ProductoExcepcion {
        gestor.darDeBajaProducto(idProducto);

        if (ConexionMySQL.estaConectado()) {
            productoDAO.darDeBajaProducto(idProducto);
        }

        return "OK: Producto ID " + idProducto + " dado de baja.";
    }

    /**
     * Retorna el catalogo completo como ArrayList (para mostrar en el menu).
     */
    public ArrayList<ProductoTecnologico> obtenerCatalogo() {
        return gestor.getCatalogo();
    }

}
