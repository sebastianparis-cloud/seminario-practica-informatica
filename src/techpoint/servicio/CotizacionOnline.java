package techpoint.servicio;

import techpoint.excepcion.ProductoExcepcion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * CotizacionOnline - implementacion de ICotizacion que consulta
 * la cotizacion del dolar blue en tiempo real via API REST.
 *
 * API utilizada: dolarapi.com (gratuita, sin autenticacion)
 * Endpoint: GET https://dolarapi.com/v1/dolares/blue
 * Respuesta JSON: { "venta": 1420.0, ... }
 *
 * Aplicacion de POLIMORFISMO: el sistema puede usar CotizacionManual
 * o CotizacionOnline de forma intercambiable a traves de ICotizacion.
 *
 * Trazabilidad: CU001 - Actualizar Estrategia de Precios (flujo online).
 */
public class CotizacionOnline implements ICotizacion {

    private static final String URL_API = "https://dolarapi.com/v1/dolares/blue";
    private double ultimaCotizacion;

    public CotizacionOnline() {
        this.ultimaCotizacion = 0;
    }

    @Override
    public double obtenerCotizacion() throws ProductoExcepcion {
        try {
            URL url = new URL(URL_API);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(5000);
            conexion.setRequestProperty("Accept", "application/json");

            int codigoRespuesta = conexion.getResponseCode();
            if (codigoRespuesta != 200) {
                throw new ProductoExcepcion(
                    "La API de cotizacion respondio con codigo: " + codigoRespuesta);
            }

            BufferedReader lector = new BufferedReader(
                new InputStreamReader(conexion.getInputStream()));
            StringBuilder respuesta = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }
            lector.close();
            conexion.disconnect();

            ultimaCotizacion = parsearVenta(respuesta.toString());
            return ultimaCotizacion;

        } catch (ProductoExcepcion e) {
            throw e;
        } catch (Exception e) {
            throw new ProductoExcepcion(
                "Error al conectar con la API de cotizacion: " + e.getMessage()
                + ". Verifique su conexion a internet.");
        }
    }

    private double parsearVenta(String json) throws ProductoExcepcion {
        String clave = "\"venta\":";
        int inicio = json.indexOf(clave);
        if (inicio == -1) {
            throw new ProductoExcepcion(
                "Formato de respuesta invalido: campo 'venta' no encontrado.");
        }
        inicio += clave.length();
        int fin = json.indexOf(",", inicio);
        if (fin == -1) fin = json.indexOf("}", inicio);
        if (fin == -1) {
            throw new ProductoExcepcion(
                "Formato de respuesta invalido: no se pudo extraer el valor de venta.");
        }
        String valorStr = json.substring(inicio, fin).trim();
        try {
            return Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            throw new ProductoExcepcion(
                "Valor de cotizacion invalido recibido de la API: " + valorStr);
        }
    }

    @Override
    public boolean esCotizacionVigente() {
        // La cotizacion online siempre es vigente (se acaba de obtener de la API)
        return ultimaCotizacion > 0;
    }

    public double getUltimaCotizacion() {
        return ultimaCotizacion;
    }
}
