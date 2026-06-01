package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

public class Consola extends ProductoTecnologico {

    private String fabricante;
    private String generacion;

    public Consola(int id, String nombre, double costoUSD,
                   double margenSeguridad, String fabricante, String generacion)
            throws ProductoExcepcion {
        super(id, nombre, costoUSD, margenSeguridad, "Gaming");
        this.fabricante = fabricante;
        this.generacion = generacion;
    }

    @Override
    public String obtenerDescripcionCategoria() {
        return "Consola " + fabricante + " | " + generacion
               + " | Costos elevados en USD - alta demanda estacional.";
    }

    public String getFabricante() { return fabricante; }
    public String getGeneracion() { return generacion; }
}
