package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

public class Accesorio extends ProductoTecnologico {

    private String subcategoria;
    private String descripcion;

    public Accesorio(int id, String nombre, double costoUSD,
                     double margenSeguridad, String subcategoria, String descripcion)
            throws ProductoExcepcion {
        super(id, nombre, costoUSD, margenSeguridad, subcategoria);
        this.subcategoria = subcategoria;
        this.descripcion = descripcion;
    }

    @Override
    public String obtenerDescripcionCategoria() {
        return subcategoria + " | " + descripcion
               + " | Margenes superiores al promedio en esta categoria.";
    }

    public String getSubcategoria() { return subcategoria; }
    public String getDescripcion() { return descripcion; }
}
