package techpoint.menu;

import techpoint.controlador.ControladorCanales;
import techpoint.controlador.ControladorCatalogo;
import techpoint.controlador.ControladorPrecios;
import techpoint.controlador.ControladorRentabilidad;
import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.CanalVenta;
import techpoint.modelo.GestorRentabilidad;
import techpoint.modelo.ProductoTecnologico;
import techpoint.persistencia.CanalDAO;
import techpoint.persistencia.ConexionMySQL;
import techpoint.persistencia.ProductoDAO;
import techpoint.servicio.CotizacionManual;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * MenuPrincipal - Vista del patron MVC del sistema TechPoint (TP4).
 *
 * En el TP4, MenuPrincipal actua como la VISTA del MVC:
 *   - Solo captura la entrada del usuario y muestra resultados.
 *   - Delega toda la logica a los Controladores.
 *   - No contiene logica de negocio ni SQL directamente.
 *
 * Cambios respecto al TP3:
 *   - Instancia y usa los 4 controladores del paquete techpoint.controlador
 *   - Agrega opcion 5: Gestionar Canales de Venta (CU005 - nuevo en TP4)
 *   - Toda operacion persiste en MySQL via DAO al confirmar
 *   - Agrega opcion de reporte con arreglo nativo de precios
 *
 * Trazabilidad: Vista del MVC — CU001, CU002, CU003, CU004, CU005.
 */
public class MenuPrincipal {

    // MODELO
    private GestorRentabilidad gestor;

    // CONTROLADORES (capa Controlador del MVC)
    private ControladorPrecios ctrlPrecios;
    private ControladorCatalogo ctrlCatalogo;
    private ControladorRentabilidad ctrlRentabilidad;
    private ControladorCanales ctrlCanales;

    private Scanner scanner;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
        inicializarSistema();
    }

    /**
     * Inicializa el sistema con doble modo: MySQL o datos en memoria.
     * En TP4, si MySQL esta activo los datos vienen de la BD.
     * Si no, carga datos de prueba como fallback.
     */
    private void inicializarSistema() {
        try {
            CotizacionManual cotizacion = new CotizacionManual(1420.00);
            gestor = new GestorRentabilidad(cotizacion);
            gestor.actualizarCotizacionCache(1420.00);

            // Instanciar controladores (MVC)
            ctrlPrecios      = new ControladorPrecios(gestor);
            ctrlCatalogo     = new ControladorCatalogo(gestor);
            ctrlRentabilidad = new ControladorRentabilidad(gestor);
            ctrlCanales      = new ControladorCanales(gestor);

            // Intentar cargar datos desde MySQL
            ProductoDAO productoDAO = new ProductoDAO();
            CanalDAO canalDAO = new CanalDAO();

            if (productoDAO.verificarConexion()) {
                System.out.println("  Conexion MySQL: ACTIVA");

                ArrayList<ProductoTecnologico> productos = productoDAO.cargarProductos();
                ArrayList<CanalVenta> canales = canalDAO.cargarCanales();

                if (!productos.isEmpty()) {
                    for (ProductoTecnologico p : productos) gestor.agregarProducto(p);
                    System.out.println("  Productos cargados desde MySQL: " + productos.size());
                } else {
                    cargarProductosPrueba();
                }

                if (!canales.isEmpty()) {
                    for (CanalVenta c : canales) gestor.agregarCanal(c);
                    System.out.println("  Canales cargados desde MySQL: " + canales.size());
                } else {
                    cargarCanalesPrueba();
                }

            } else {
                System.out.println("  Conexion MySQL: NO DISPONIBLE - usando datos en memoria.");
                cargarProductosPrueba();
                cargarCanalesPrueba();
            }

            System.out.println("  Productos: " + gestor.getCatalogo().size()
                + " | Canales: " + gestor.getCanales().size());

            // Obtener cotizacion al arranque (online o manual como fallback)
            System.out.println();
            String resumen = ctrlPrecios.sincronizarPreciosAutomatico(scanner);
            System.out.println("  " + resumen);

        } catch (ProductoExcepcion e) {
            System.out.println("[ERROR en inicializacion] " + e.getMessage());
        }
    }

    private void cargarProductosPrueba() throws ProductoExcepcion {
        gestor.agregarProducto(new techpoint.modelo.Smartphone(1, "iPhone 15 128GB", 650.00, 25.00, "Apple", 128));
        gestor.agregarProducto(new techpoint.modelo.Smartphone(2, "Samsung Galaxy S24", 550.00, 25.00, "Samsung", 256));
        gestor.agregarProducto(new techpoint.modelo.Consola(3, "PlayStation 5", 450.00, 20.00, "Sony", "9na generacion"));
        gestor.agregarProducto(new techpoint.modelo.Accesorio(4, "Sony WH-1000XM5", 280.00, 30.00, "Audio", "Auriculares inalambricos de alta fidelidad"));
        gestor.agregarProducto(new techpoint.modelo.Accesorio(5, "DJI Mini 4 Pro", 760.00, 35.00, "Hogar y Tecnologia", "Drone plegable con camara 4K"));
        gestor.agregarProducto(new techpoint.modelo.Notebook(6, "MacBook Air M2", 1100.00, 28.00, "Apple M2", 8));
        System.out.println("  Datos de prueba cargados en memoria.");
    }

    private void cargarCanalesPrueba() throws ProductoExcepcion {
        gestor.agregarCanal(new CanalVenta(1, "Marketplace", 12.00));
        gestor.agregarCanal(new CanalVenta(2, "Mercado Libre", 17.00));
        gestor.agregarCanal(new CanalVenta(3, "Redes Sociales", 5.00));
    }

    // ==========================================================
    // MENU PRINCIPAL
    // ==========================================================

    public void ejecutar() {
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1: ejecutarCU001(); break;
                case 2: ejecutarCU002(); break;
                case 3: ejecutarCU003(); break;
                case 4: ejecutarCU004(); break;
                case 5: ejecutarCU005(); break;
                case 6: ejecutarReportePrecios(); break;
                case 0:
                    System.out.println();
                    System.out.println("========================================");
                    System.out.println("  TechPoint - Sistema finalizado.");
                    ConexionMySQL.cerrarConexion();
                    System.out.println("========================================");
                    ejecutando = false;
                    break;
                default:
                    System.out.println("  Opcion invalida. Ingrese del 0 al 6.");
            }
        }
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   TECHPOINT - Gestor de Rentabilidad  ");
        System.out.println("   Sistema de Gestion de Precios v1.0  ");
        System.out.println("========================================");
        System.out.println("  Cotizacion USD: $"
            + String.format("%.2f", gestor.getCotizacionCache())
            + (ConexionMySQL.estaConectado() ? " | MySQL: ACTIVO" : " | MySQL: MEMORIA"));
        System.out.println("----------------------------------------");
        System.out.println("  1. Actualizar Cotizacion y Precios");
        System.out.println("  2. Consultar Rentabilidad por Producto");
        System.out.println("  3. Gestionar Catalogo de Productos");
        System.out.println("  4. Analizar Precios por Canal de Venta");
        System.out.println("  5. Gestionar Canales de Venta");
        System.out.println("  6. Reporte de Precios por Producto");
        System.out.println("  0. Salir");
        System.out.println("----------------------------------------");
        System.out.print("  Seleccione una opcion: ");
    }

    // ==========================================================
    // CU001 — ACTUALIZAR ESTRATEGIA DE PRECIOS
    // ==========================================================

    private void ejecutarCU001() {
        System.out.println();
        System.out.println("--- Actualizar Cotizacion y Precios ---");
        System.out.println("  1. Sincronizar con cotizacion online (Dolar Blue)");
        System.out.println("  2. Ingresar cotizacion manualmente");
        System.out.println("  0. Volver");
        System.out.print("  Opcion: ");

        int sub = leerOpcion();
        switch (sub) {
            case 1:
                System.out.println("  Consultando API...");
                System.out.println("  " + ctrlPrecios.sincronizarPreciosOnline());
                System.out.println("  Cotizacion vigente: $"
                    + String.format("%.2f", gestor.getCotizacionCache()));
                break;
            case 2:
                System.out.print("  Ingrese cotizacion USD/ARS: $");
                double valor = 0;
                while (valor <= 0) {
                    try {
                        valor = scanner.nextDouble(); scanner.nextLine();
                        if (valor <= 0)
                            System.out.print("  El valor debe ser mayor a cero: $");
                    } catch (InputMismatchException e) {
                        System.out.print("  [ERROR] Ingrese un numero valido: $");
                        scanner.nextLine();
                    }
                }
                System.out.println("  " + ctrlPrecios.actualizarCotizacionManual(valor));
                System.out.println("  Cotizacion vigente: $"
                    + String.format("%.2f", gestor.getCotizacionCache()));
                break;
            case 0:
                break;
            default:
                System.out.println("  Opcion invalida.");
        }
    }

    // ==========================================================
    // CU002 — CONSULTAR RENTABILIDAD POR PRODUCTO
    // ==========================================================

    private void ejecutarCU002() {
        System.out.println();
        System.out.println("--- Consultar Rentabilidad por Producto ---");
        ctrlRentabilidad.listarProductos(null);
        System.out.print("  ID del producto (0 para volver): ");
        try {
            int id = scanner.nextInt();
            if (id != 0) ctrlRentabilidad.consultarRentabilidad(id);
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
        } catch (ProductoExcepcion e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ==========================================================
    // CU003 — GESTIONAR CATALOGO TECNOLOGICO
    // ==========================================================

    private void ejecutarCU003() {
        boolean activo = true;
        while (activo) {
            System.out.println();
            System.out.println("--- Gestionar Catalogo de Productos ---");
            System.out.println("  1. Ver catalogo completo");
            System.out.println("  2. Filtrar por categoria");
            System.out.println("  3. Agregar producto");
            System.out.println("  4. Dar de baja un producto");
            System.out.println("  0. Volver");
            System.out.print("  Opcion: ");

            int sub = leerOpcion();
            switch (sub) {
                case 1:
                    ctrlRentabilidad.listarProductos(null);
                    break;
                case 2:
                    String[] cats = ctrlCatalogo.obtenerCategorias();
                    System.out.println("  Categorias disponibles:");
                    for (int i = 0; i < cats.length; i++) {
                        System.out.println("    " + (i + 1) + ". " + cats[i]);
                    }
                    System.out.print("  Seleccione (1-" + cats.length + "): ");
                    try {
                        int idx = scanner.nextInt() - 1;
                        if (idx >= 0 && idx < cats.length) {
                            ctrlRentabilidad.listarProductos(cats[idx]);
                        } else {
                            System.out.println("  Opcion invalida.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] Ingrese un numero."); scanner.nextLine();
                    }
                    break;
                case 3:
                    altaProductoDesdeConsola();
                    break;
                case 4:
                    ctrlRentabilidad.listarProductos(null);
                    System.out.print("  ID a dar de baja: ");
                    try {
                        int id = scanner.nextInt();
                        System.out.println("  " + ctrlCatalogo.bajaProducto(id));
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
                    } catch (ProductoExcepcion e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                    break;
                case 0: activo = false; break;
                default: System.out.println("  Opcion invalida.");
            }
        }
    }

    private void altaProductoDesdeConsola() {
        System.out.println();
        System.out.println("  === Alta de Nuevo Producto ===");
        try {
            scanner.nextLine();
            System.out.print("  Nombre: "); String nombre = scanner.nextLine().trim();
            System.out.print("  Costo USD: $"); double costo = scanner.nextDouble();
            System.out.print("  Margen (%): "); double margen = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("  Tipo de producto:");
            System.out.println("    1. Smartphone (Mobile y Wearables)");
            System.out.println("    2. Consola (Gaming)");
            System.out.println("    3. Accesorio Audio");
            System.out.println("    4. Accesorio Hogar y Tecnologia");
            System.out.println("    5. Notebook (Computacion)");
            System.out.print("  Opcion: ");
            int tipo = scanner.nextInt(); scanner.nextLine();

            String p1 = "", p2 = "";
            switch (tipo) {
                case 1:
                    System.out.print("  Marca: "); p1 = scanner.nextLine();
                    System.out.print("  Almacenamiento (GB): "); p2 = scanner.nextLine();
                    break;
                case 2:
                    System.out.print("  Fabricante: "); p1 = scanner.nextLine();
                    System.out.print("  Generacion: "); p2 = scanner.nextLine();
                    break;
                case 3: case 4:
                    System.out.print("  Descripcion: "); p1 = scanner.nextLine();
                    break;
                case 5:
                    System.out.print("  Procesador: "); p1 = scanner.nextLine();
                    System.out.print("  RAM (GB): "); p2 = scanner.nextLine();
                    break;
                default:
                    System.out.println("  Tipo invalido."); return;
            }

            System.out.println("  " + ctrlCatalogo.altaProducto(nombre, costo, margen, tipo, p1, p2));

        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] Dato invalido. Operacion cancelada.");
            scanner.nextLine();
        } catch (ProductoExcepcion e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ==========================================================
    // CU004 — ANALIZAR DIFERENCIA POR CANAL
    // ==========================================================

    private void ejecutarCU004() {
        System.out.println();
        System.out.println("--- Analizar Precios por Canal de Venta ---");
        ctrlRentabilidad.listarProductos(null);
        System.out.print("  ID del producto (0 para volver): ");
        try {
            int id = scanner.nextInt();
            if (id != 0) ctrlCanales.analizarPorCanal(id);
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
        } catch (ProductoExcepcion e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ==========================================================
    // CU005 — GESTIONAR CANALES DE VENTA (nuevo en TP4)
    // ==========================================================

    private void ejecutarCU005() {
        boolean activo = true;
        while (activo) {
            System.out.println();
            System.out.println("--- Gestionar Canales de Venta ---");
            ctrlCanales.listarCanales();
            System.out.println();
            System.out.println("  1. Agregar canal");
            System.out.println("  2. Modificar comision");
            System.out.println("  3. Dar de baja canal");
            System.out.println("  0. Volver");
            System.out.print("  Opcion: ");

            int sub = leerOpcion();
            switch (sub) {
                case 1:
                    System.out.println("  === Alta de Canal ===");
                    try {
                        scanner.nextLine();
                        System.out.print("  Nombre del canal: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("  Comision (%): ");
                        double com = scanner.nextDouble(); scanner.nextLine();
                        System.out.println("  " + ctrlCanales.altaCanal(nombre, com));
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] Dato invalido."); scanner.nextLine();
                    } catch (ProductoExcepcion e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("  ID del canal a modificar: ");
                    try {
                        int id = scanner.nextInt();
                        System.out.print("  Nueva comision (%): ");
                        double nuevaCom = scanner.nextDouble(); scanner.nextLine();
                        System.out.println("  " + ctrlCanales.modificarComision(id, nuevaCom));
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] Dato invalido."); scanner.nextLine();
                    } catch (ProductoExcepcion e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("  ID del canal a dar de baja: ");
                    try {
                        int id = scanner.nextInt(); scanner.nextLine();
                        System.out.println("  " + ctrlCanales.bajaCanal(id));
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
                    } catch (ProductoExcepcion e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                    break;
                case 0: activo = false; break;
                default: System.out.println("  Opcion invalida.");
            }
        }
    }

    // ==========================================================
    // REPORTE DE PRECIOS CON ARREGLO NATIVO (nuevo en TP4)
    // ==========================================================

    /**
     * Muestra el reporte de precios usando el arreglo nativo double[]
     * generado por GestorRentabilidad.generarArregloPrecios().
     *
     * Evidencia el uso complementario de arreglos y ArrayList:
     * - ArrayList: almacena los objetos ProductoTecnologico en el gestor.
     * - double[]: arreglo nativo que contiene solo los precios calculados.
     */
    private void ejecutarReportePrecios() {
        System.out.println();
        System.out.println("--- Reporte de Precios por Producto ---");

        if (gestor.getCotizacionCache() <= 0) {
            System.out.println("  [ERROR] Sincronice los precios primero (opcion 1).");
            return;
        }

        try {
            double[] precios = gestor.generarArregloPrecios(gestor.getCotizacionCache());
            ArrayList<ProductoTecnologico> catalogo = ctrlCatalogo.obtenerCatalogo();

            System.out.println("  Cotizacion aplicada: $"
                + String.format("%.2f", gestor.getCotizacionCache()));
            System.out.println("  Productos activos: " + precios.length);
            System.out.println();
            System.out.printf("  %-30s %s%n", "PRODUCTO", "PRECIO SUGERIDO ARS");
            System.out.println("  " + "-".repeat(55));

            int indiceArreglo = 0;
            for (ProductoTecnologico p : catalogo) {
                if (p.isActivo() && indiceArreglo < precios.length) {
                    System.out.printf("  %-30s $%,.2f%n",
                        p.getNombre(), precios[indiceArreglo]);
                    indiceArreglo++;
                }
            }

            double suma = 0;
            for (double precio : precios) {
                suma += precio;
            }
            double promedio = precios.length > 0 ? suma / precios.length : 0;
            System.out.println("  " + "-".repeat(55));
            System.out.printf("  %-30s $%,.2f%n", "PRECIO PROMEDIO:", promedio);

        } catch (ProductoExcepcion e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ==========================================================
    // UTILITARIO
    // ==========================================================

    private int leerOpcion() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] Ingrese un numero valido.");
            scanner.nextLine();
            return -1;
        }
    }
}
