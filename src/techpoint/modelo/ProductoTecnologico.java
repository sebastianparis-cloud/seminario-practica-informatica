package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

public abstract class ProductoTecnologico {

    private int id;
    private String nombre;
    private double costoUSD;
    private double costoARS;
    private double margenSeguridad;
    private boolean activo;
    private String categoria;

    public static final double MARGEN_MINIMO = 10.0;

    public ProductoTecnologico(int id, String nombre, double costoUSD,
                                double margenSeguridad, String categoria)
            throws ProductoExcepcion {
        if (nombre == null || nombre.trim().isEmpty())
            throw new ProductoExcepcion("El nombre del producto no puede estar vacio.");
        if (costoUSD <= 0)
            throw new ProductoExcepcion(
                "El costo en USD debe ser mayor a cero. Valor recibido: " + costoUSD);
        if (margenSeguridad < MARGEN_MINIMO)
            throw new ProductoExcepcion(
                "El margen no puede ser menor al minimo de " + MARGEN_MINIMO
                + "%. Margen recibido: " + margenSeguridad + "%");
        if (categoria == null || categoria.trim().isEmpty())
            throw new ProductoExcepcion("La categoria no puede estar vacia.");

        this.id = id;
        this.nombre = nombre.trim();
        this.costoUSD = costoUSD;
        this.costoARS = 0.0;
        this.margenSeguridad = margenSeguridad;
        this.activo = true;
        this.categoria = categoria.trim();
    }

    public abstract String obtenerDescripcionCategoria();

    public double calcularPrecioSugerido(double valorUSD) throws ProductoExcepcion {
        if (valorUSD <= 0)
            throw new ProductoExcepcion("La cotizacion debe ser mayor a cero.");
        this.costoARS = this.costoUSD * valorUSD;
        return this.costoARS * (1 + this.margenSeguridad / 100.0);
    }

    public boolean esRentable(double precioVenta) {
        if (costoARS <= 0) return false;
        double margenReal = ((precioVenta - costoARS) / costoARS) * 100.0;
        return margenReal >= MARGEN_MINIMO;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | Categoria: %s | Costo: USD %.2f | Margen: %.1f%% | %s",
            id, nombre, categoria, costoUSD, margenSeguridad,
            activo ? "ACTIVO" : "INACTIVO");
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) throws ProductoExcepcion {
        if (nombre == null || nombre.trim().isEmpty())
            throw new ProductoExcepcion("El nombre no puede estar vacio.");
        this.nombre = nombre.trim();
    }
    public double getCostoUSD() { return costoUSD; }
    public void setCostoUSD(double costoUSD) throws ProductoExcepcion {
        if (costoUSD <= 0) throw new ProductoExcepcion("El costo USD debe ser mayor a cero.");
        this.costoUSD = costoUSD;
    }
    public double getCostoARS() { return costoARS; }
    public void setCostoARS(double costoARS) { this.costoARS = costoARS; }
    public double getMargenSeguridad() { return margenSeguridad; }
    public void setMargenSeguridad(double m) throws ProductoExcepcion {
        if (m < MARGEN_MINIMO)
            throw new ProductoExcepcion("El margen no puede ser menor al minimo de " + MARGEN_MINIMO + "%.");
        this.margenSeguridad = m;
    }
    public boolean isActivo() { return activo; }
    public void darDeBaja() { this.activo = false; }
    public void reactivar() { this.activo = true; }
    public String getCategoria() { return categoria; }
}
