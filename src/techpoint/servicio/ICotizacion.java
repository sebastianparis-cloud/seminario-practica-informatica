package techpoint.servicio;

import techpoint.excepcion.ProductoExcepcion;

public interface ICotizacion {
    double obtenerCotizacion() throws ProductoExcepcion;
    boolean esCotizacionVigente();
}
