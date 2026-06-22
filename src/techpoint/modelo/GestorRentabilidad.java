package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.servicio.ICotizacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestorRentabilidad {

    private ArrayList<ProductoTecnologico> catalogo;
    private ArrayList<CanalVenta> canales;
    private ICotizacion servicioCotizacion;
    private double cotizacionCache;

    public GestorRentabilidad(ICotizacion servicioCotizacion) {
        this.catalogo = new ArrayList<>();
        this.canales = new ArrayList<>();
        this.servicioCotizacion = servicioCotizacion;
        this.cotizacionCache = 0.0;
    }

    // CU001 - Actualizar Estrategia de Precios
    public void sincronizarEstrategiaDePrecios() throws ProductoExcepcion {
        if (!servicioCotizacion.esCotizacionVigente())
            System.out.println("  [ADVERTENCIA] La cotizacion supera las 24 horas de antiguedad.");
        this.cotizacionCache = servicioCotizacion.obtenerCotizacion();
        if (catalogo.isEmpty())
            throw new ProductoExcepcion("El catalogo esta vacio. Agregue productos antes de sincronizar.");
        int actualizados = 0;
        for (ProductoTecnologico p : catalogo) {
            if (p.isActivo()) {
                p.setCostoARS(p.getCostoUSD() * cotizacionCache);
                actualizados++;
            }
        }
        System.out.println("  Cotizacion USD: $" + String.format("%.2f", cotizacionCache));
        System.out.println("  Productos actualizados: " + actualizados);
    }

    // CU002 - Consultar Rentabilidad por Producto
    public void consultarRentabilidad(int idProducto) throws ProductoExcepcion {
        ProductoTecnologico p = buscarProductoPorId(idProducto);
        if (p == null) throw new ProductoExcepcion("No se encontro el producto con ID: " + idProducto);
        if (!p.isActivo()) throw new ProductoExcepcion("El producto '" + p.getNombre() + "' esta dado de baja.");
        if (cotizacionCache <= 0)
            throw new ProductoExcepcion("No hay cotizacion cargada. Ejecute primero la opcion 1.");
        if (!servicioCotizacion.esCotizacionVigente())
            System.out.println("  [ADVERTENCIA] Cotizacion puede estar desactualizada (>24 hs).");
        double precio = p.calcularPrecioSugerido(cotizacionCache);
        double costo = p.getCostoARS();
        System.out.println();
        System.out.println("  Producto      : " + p.getNombre());
        System.out.println("  Categoria     : " + p.getCategoria());
        System.out.println("  Costo USD     : $" + String.format("%.2f", p.getCostoUSD()));
        System.out.println("  Cotizacion    : $" + String.format("%.2f", cotizacionCache));
        System.out.println("  Costo ARS     : $" + String.format("%.2f", costo));
        System.out.println("  Margen        : " + p.getMargenSeguridad() + "%");
        System.out.println("  Precio suger. : $" + String.format("%.2f", precio));
        System.out.println("  Ganancia proy.: $" + String.format("%.2f", precio - costo));
        System.out.println("  Estado        : " + (p.esRentable(precio) ? "DENTRO DEL MARGEN" : "FUERA DEL MARGEN MINIMO"));
        System.out.println("  Descripcion   : " + p.obtenerDescripcionCategoria());
    }

    // CU003 - Gestionar Catalogo Tecnologico
    public void agregarProducto(ProductoTecnologico producto) throws ProductoExcepcion {
        if (producto == null) throw new ProductoExcepcion("El producto no puede ser nulo.");
        if (buscarProductoPorId(producto.getId()) != null)
            throw new ProductoExcepcion("Ya existe un producto con ID " + producto.getId());
        catalogo.add(producto);
        System.out.println("  Producto '" + producto.getNombre() + "' registrado correctamente.");
    }

    public void darDeBajaProducto(int idProducto) throws ProductoExcepcion {
        ProductoTecnologico p = buscarProductoPorId(idProducto);
        if (p == null) throw new ProductoExcepcion("No se encontro el producto con ID: " + idProducto);
        if (!p.isActivo()) throw new ProductoExcepcion("El producto '" + p.getNombre() + "' ya esta inactivo.");
        p.darDeBaja();
        System.out.println("  Producto '" + p.getNombre() + "' dado de baja correctamente.");
    }

    public void listarProductos(String categoria) {
        System.out.println();
        boolean hay = false;
        for (ProductoTecnologico p : catalogo) {
            if (categoria == null || categoria.isEmpty() || p.getCategoria().equalsIgnoreCase(categoria)) {
                System.out.println("  " + p.toString());
                hay = true;
            }
        }
        if (!hay) System.out.println("  No se encontraron productos" +
            (categoria != null && !categoria.isEmpty() ? " en: " + categoria : "") + ".");
    }

    // CU004 - Analizar Diferencia por Canal de Venta
    public void analizarPorCanal(int idProducto) throws ProductoExcepcion {
        ProductoTecnologico p = buscarProductoPorId(idProducto);
        if (p == null) throw new ProductoExcepcion("No se encontro el producto con ID: " + idProducto);
        if (!p.isActivo()) throw new ProductoExcepcion("El producto '" + p.getNombre() + "' esta dado de baja.");
        if (cotizacionCache <= 0)
            throw new ProductoExcepcion("No hay cotizacion cargada. Ejecute primero la opcion 1.");
        if (canales.isEmpty()) throw new ProductoExcepcion("No hay canales configurados.");

        double precio = p.calcularPrecioSugerido(cotizacionCache);
        double costo = p.getCostoARS();

        List<CanalVenta> activos = new ArrayList<>();
        for (CanalVenta c : canales) { if (c.isActivo()) activos.add(c); }
        activos.sort(Comparator.comparingDouble(c -> -c.calcularGananciaNeta(precio)));

        System.out.println();
        System.out.println("  Producto      : " + p.getNombre());
        System.out.println("  Precio suger. : $" + String.format("%.2f", precio));
        System.out.println("  Costo ARS     : $" + String.format("%.2f", costo));
        System.out.println();
        System.out.printf("  %-20s | %-12s | %-14s | %s%n", "CANAL", "COMISION", "GANANCIA NETA", "ESTADO");
        System.out.println("  " + "-".repeat(68));

        for (int i = 0; i < activos.size(); i++) {
            CanalVenta c = activos.get(i);
            double ganancia = c.calcularGananciaNeta(precio);
            boolean perdida = c.generaPerdida(precio, costo);
            String estado = (i == 0 && !perdida) ? "MAS CONVENIENTE"
                          : perdida ? "VENTA A PERDIDA" : "RENTABLE";
            System.out.printf("  %-20s | %10.1f%% | $%12.2f | %s%n",
                c.getNombre(), c.getComisionPct(), ganancia, estado);
        }
        System.out.println();
        for (CanalVenta c : activos) {
            if (c.generaPerdida(precio, costo)) {
                double minimo = costo / (1 - c.getComisionPct() / 100.0);
                System.out.printf("  [SUGERENCIA] Precio minimo para %s: $%.2f%n", c.getNombre(), minimo);
            }
        }
    }

    // Metodos auxiliares
    public ProductoTecnologico buscarProductoPorId(int id) {
        for (ProductoTecnologico p : catalogo) { if (p.getId() == id) return p; }
        return null;
    }

    public void agregarCanal(CanalVenta canal) throws ProductoExcepcion {
        if (canal == null) throw new ProductoExcepcion("El canal no puede ser nulo.");
        for (CanalVenta c : canales) {
            if (c.getId() == canal.getId())
                throw new ProductoExcepcion("Ya existe un canal con ID: " + canal.getId());
        }
        canales.add(canal);
    }

    public void actualizarCotizacionCache(double valor) throws ProductoExcepcion {
        if (valor <= 0) throw new ProductoExcepcion("La cotizacion debe ser mayor a cero.");
        this.cotizacionCache = valor;
    }

    /**
     * Genera un resumen de precios del catalogo activo en arreglo nativo double[].
     *
     * Uso de arreglo nativo (double[]) como complemento al ArrayList,
     * segun requisito del TP4: "arreglos y ArrayList de forma complementaria".
     *
     * @param valorUSD cotizacion vigente
     * @return double[] con precio sugerido de cada producto activo en orden
     * @throws ProductoExcepcion si la cotizacion es invalida
     */
    public double[] generarArregloPrecios(double valorUSD) throws ProductoExcepcion {
        if (valorUSD <= 0)
            throw new ProductoExcepcion("La cotizacion debe ser mayor a cero.");

        int cantActivos = 0;
        for (ProductoTecnologico p : catalogo) {
            if (p.isActivo()) cantActivos++;
        }

        double[] precios = new double[cantActivos];

        int indice = 0;
        for (ProductoTecnologico p : catalogo) {
            if (p.isActivo()) {
                precios[indice] = p.calcularPrecioSugerido(valorUSD);
                indice++;
            }
        }

        return precios;
    }

    /**
     * Elimina un canal del ArrayList en memoria (para sincronizar con baja en MySQL).
     */
    public void removerCanalPorId(int idCanal) {
        canales.removeIf(c -> c.getId() == idCanal);
    }

    public ArrayList<ProductoTecnologico> getCatalogo() { return catalogo; }
    public ArrayList<CanalVenta> getCanales() { return canales; }
    public double getCotizacionCache() { return cotizacionCache; }
    public ICotizacion getServicioCotizacion() { return servicioCotizacion; }
}
