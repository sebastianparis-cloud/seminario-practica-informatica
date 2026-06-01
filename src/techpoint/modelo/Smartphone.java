package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

public class Smartphone extends ProductoTecnologico {

    private String marca;
    private int almacenamientoGB;

    public Smartphone(int id, String nombre, double costoUSD,
                      double margenSeguridad, String marca, int almacenamientoGB)
            throws ProductoExcepcion {
        super(id, nombre, costoUSD, margenSeguridad, "Mobile y Wearables");
        this.marca = marca;
        this.almacenamientoGB = almacenamientoGB;
    }

    @Override
    public String obtenerDescripcionCategoria() {
        return "Smartphone " + marca + " | Almacenamiento: " + almacenamientoGB
               + " GB | Alta rotacion - margenes variables segun prestaciones.";
    }

    public String getMarca() { return marca; }
    public int getAlmacenamientoGB() { return almacenamientoGB; }
}
