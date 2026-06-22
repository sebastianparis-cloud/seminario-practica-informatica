package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

/**
 * Notebook - cuarta subclase concreta de ProductoTecnologico.
 *
 * Aplicacion de HERENCIA: extiende ProductoTecnologico.
 * Aplicacion de POLIMORFISMO: implementa obtenerDescripcionCategoria().
 *
 * Agrega la categoria "Computacion" al catalogo TechPoint en el TP4.
 * Trazabilidad: CU003, RFS05.
 */
public class Notebook extends ProductoTecnologico {

    private String procesador;
    private int ramGB;

    public Notebook(int id, String nombre, double costoUSD,
                    double margenSeguridad, String procesador, int ramGB)
            throws ProductoExcepcion {
        super(id, nombre, costoUSD, margenSeguridad, "Computacion");
        this.procesador = procesador;
        this.ramGB = ramGB;
    }

    @Override
    public String obtenerDescripcionCategoria() {
        return "Notebook | Procesador: " + procesador + " | RAM: " + ramGB + " GB"
               + " | Alta demanda corporativa y educativa.";
    }

    public String getProcesador() { return procesador; }
    public int getRamGB() { return ramGB; }
}
