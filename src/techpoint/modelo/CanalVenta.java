package techpoint.modelo;

import techpoint.excepcion.ProductoExcepcion;

public class CanalVenta {

    private int id;
    private String nombre;
    private double comisionPct;
    private boolean activo;

    public CanalVenta(int id, String nombre, double comisionPct)
            throws ProductoExcepcion {
        if (nombre == null || nombre.trim().isEmpty())
            throw new ProductoExcepcion("El nombre del canal no puede estar vacio.");
        if (comisionPct < 0 || comisionPct >= 100)
            throw new ProductoExcepcion(
                "La comision debe estar entre 0% y 100%. Valor recibido: " + comisionPct + "%");
        this.id = id;
        this.nombre = nombre.trim();
        this.comisionPct = comisionPct;
        this.activo = true;
    }

    public double calcularGananciaNeta(double precioSugeridoARS) {
        return precioSugeridoARS - (precioSugeridoARS * (comisionPct / 100.0));
    }

    public boolean generaPerdida(double precioSugeridoARS, double costoARS) {
        return calcularGananciaNeta(precioSugeridoARS) < costoARS;
    }

    @Override
    public String toString() {
        return String.format("%-20s | Comision: %5.1f%% | %s",
            nombre, comisionPct, activo ? "ACTIVO" : "INACTIVO");
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getComisionPct() { return comisionPct; }
    public boolean isActivo() { return activo; }
}
