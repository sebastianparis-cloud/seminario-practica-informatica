package techpoint.servicio;

import techpoint.excepcion.ProductoExcepcion;

public class CotizacionManual implements ICotizacion {

    private double valorUSD;
    private boolean vigente;
    private long timestampCarga;

    private static final long HORAS_VIGENCIA = 24;
    private static final long MS_POR_HORA = 3_600_000L;

    public CotizacionManual(double valorUSD) throws ProductoExcepcion {
        if (valorUSD <= 0) {
            throw new ProductoExcepcion(
                "El valor del dolar debe ser mayor a cero. Valor recibido: " + valorUSD);
        }
        this.valorUSD = valorUSD;
        this.vigente = true;
        this.timestampCarga = System.currentTimeMillis();
    }

    @Override
    public double obtenerCotizacion() throws ProductoExcepcion {
        if (valorUSD <= 0) {
            throw new ProductoExcepcion("Cotizacion no inicializada. Ingrese el valor del dolar.");
        }
        return valorUSD;
    }

    @Override
    public boolean esCotizacionVigente() {
        long ahora = System.currentTimeMillis();
        return (ahora - timestampCarga) < (HORAS_VIGENCIA * MS_POR_HORA);
    }

    public double getValorUSD() { return valorUSD; }

    public void actualizarCotizacion(double nuevoValor) throws ProductoExcepcion {
        if (nuevoValor <= 0) {
            throw new ProductoExcepcion(
                "El nuevo valor del dolar debe ser mayor a cero. Valor recibido: " + nuevoValor);
        }
        this.valorUSD = nuevoValor;
        this.timestampCarga = System.currentTimeMillis();
        this.vigente = true;
    }
}
