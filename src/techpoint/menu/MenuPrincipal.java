package techpoint.menu;

import techpoint.excepcion.ProductoExcepcion;
import techpoint.modelo.Accesorio;
import techpoint.modelo.CanalVenta;
import techpoint.modelo.Consola;
import techpoint.modelo.GestorRentabilidad;
import techpoint.modelo.ProductoTecnologico;
import techpoint.modelo.Smartphone;
import techpoint.servicio.CotizacionManual;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuPrincipal {

    private GestorRentabilidad gestor;
    private Scanner scanner;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
        inicializarSistema();
    }

    private void inicializarSistema() {
        try {
            CotizacionManual cotizacion = new CotizacionManual(1180.00);
            gestor = new GestorRentabilidad(cotizacion);
            gestor.actualizarCotizacionCache(1180.00);
            gestor.agregarCanal(new CanalVenta(1, "Marketplace", 12.00));
            gestor.agregarCanal(new CanalVenta(2, "Mercado Libre", 17.00));
            gestor.agregarCanal(new CanalVenta(3, "Redes Sociales", 5.00));
            gestor.agregarProducto(new Smartphone(1, "iPhone 15 128GB", 650.00, 25.00, "Apple", 128));
            gestor.agregarProducto(new Smartphone(2, "Samsung Galaxy S24", 550.00, 25.00, "Samsung", 256));
            gestor.agregarProducto(new Consola(3, "PlayStation 5", 450.00, 20.00, "Sony", "9na generacion"));
            gestor.agregarProducto(new Accesorio(4, "Sony WH-1000XM5", 280.00, 30.00, "Audio", "Auriculares inalambricos de alta fidelidad"));
            gestor.agregarProducto(new Accesorio(5, "DJI Mini 4 Pro", 760.00, 35.00, "Hogar y Tecnologia", "Drone plegable con camara 4K"));
            System.out.println("Sistema TechPoint iniciado. Cotizacion: $1.180,00 | Productos: 5 | Canales: 3");
        } catch (ProductoExcepcion e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void ejecutar() {
        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenu();
            int op = leerOpcion();
            switch (op) {
                case 1: ejecutarCU001(); break;
                case 2: ejecutarCU002(); break;
                case 3: ejecutarCU003(); break;
                case 4: ejecutarCU004(); break;
                case 0:
                    System.out.println("\n  TechPoint - Sistema finalizado.");
                    ejecutando = false;
                    break;
                default:
                    System.out.println("  Opcion invalida.");
            }
        }
        scanner.close();
    }

    private void mostrarMenu() {
        System.out.println("\n========================================");
        System.out.println("   TECHPOINT - Gestor de Rentabilidad  ");
        System.out.println("   USD actual: $" + String.format("%.2f", gestor.getCotizacionCache()));
        System.out.println("----------------------------------------");
        System.out.println("  1. Actualizar Estrategia de Precios    [CU001]");
        System.out.println("  2. Consultar Rentabilidad por Producto  [CU002]");
        System.out.println("  3. Gestionar Catalogo Tecnologico       [CU003]");
        System.out.println("  4. Analizar Diferencia por Canal        [CU004]");
        System.out.println("  0. Salir");
        System.out.println("----------------------------------------");
        System.out.print("  Seleccione una opcion: ");
    }

    private void ejecutarCU001() {
        System.out.println("\n--- CU001: Actualizar Estrategia de Precios ---");
        System.out.println("  1. Sincronizar  2. Ingresar manualmente  0. Volver");
        System.out.print("  Opcion: ");
        int sub = leerOpcion();
        switch (sub) {
            case 1:
                try {
                    gestor.sincronizarEstrategiaDePrecios();
                    System.out.println("  Precios actualizados correctamente.");
                } catch (ProductoExcepcion e) { System.out.println("  [ERROR] " + e.getMessage()); }
                break;
            case 2:
                System.out.print("  Ingrese valor USD en ARS: $");
                try {
                    double v = scanner.nextDouble();
                    CotizacionManual cm = (CotizacionManual) gestor.getServicioCotizacion();
                    cm.actualizarCotizacion(v);
                    gestor.actualizarCotizacionCache(v);
                    gestor.sincronizarEstrategiaDePrecios();
                    System.out.println("  Cotizacion actualizada a $" + String.format("%.2f", v));
                } catch (InputMismatchException e) {
                    System.out.println("  [ERROR] Valor numerico invalido."); scanner.nextLine();
                } catch (ProductoExcepcion e) { System.out.println("  [ERROR] " + e.getMessage()); }
                break;
            case 0: break;
            default: System.out.println("  Opcion invalida.");
        }
    }

    private void ejecutarCU002() {
        System.out.println("\n--- CU002: Consultar Rentabilidad por Producto ---");
        gestor.listarProductos(null);
        System.out.print("  ID del producto (0 para volver): ");
        try {
            int id = scanner.nextInt();
            if (id != 0) gestor.consultarRentabilidad(id);
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
        } catch (ProductoExcepcion e) { System.out.println("  [ERROR] " + e.getMessage()); }
    }

    private void ejecutarCU003() {
        boolean activo = true;
        while (activo) {
            System.out.println("\n--- CU003: Gestionar Catalogo ---");
            System.out.println("  1. Ver todo  2. Filtrar por categoria  3. Agregar  4. Dar de baja  0. Volver");
            System.out.print("  Opcion: ");
            int sub = leerOpcion();
            switch (sub) {
                case 1: gestor.listarProductos(null); break;
                case 2:
                    System.out.print("  Categoria (Mobile y Wearables / Gaming / Audio / Hogar y Tecnologia): ");
                    scanner.nextLine();
                    gestor.listarProductos(scanner.nextLine().trim());
                    break;
                case 3: agregarProducto(); break;
                case 4:
                    gestor.listarProductos(null);
                    System.out.print("  ID a dar de baja: ");
                    try {
                        gestor.darDeBajaProducto(scanner.nextInt());
                    } catch (InputMismatchException e) {
                        System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
                    } catch (ProductoExcepcion e) { System.out.println("  [ERROR] " + e.getMessage()); }
                    break;
                case 0: activo = false; break;
                default: System.out.println("  Opcion invalida.");
            }
        }
    }

    private void agregarProducto() {
        try {
            scanner.nextLine();
            System.out.print("  Nombre: "); String nombre = scanner.nextLine().trim();
            System.out.print("  Costo USD: $"); double costo = scanner.nextDouble();
            System.out.print("  Margen (%): "); double margen = scanner.nextDouble();
            scanner.nextLine();
            System.out.println("  Tipo: 1. Smartphone  2. Consola  3. Accesorio");
            System.out.print("  Opcion: "); int tipo = scanner.nextInt(); scanner.nextLine();
            int nuevoId = gestor.getCatalogo().size() + 1;
            ProductoTecnologico nuevo = null;
            if (tipo == 1) {
                System.out.print("  Marca: "); String marca = scanner.nextLine();
                System.out.print("  Almacenamiento (GB): "); int gb = scanner.nextInt(); scanner.nextLine();
                nuevo = new Smartphone(nuevoId, nombre, costo, margen, marca, gb);
            } else if (tipo == 2) {
                System.out.print("  Fabricante: "); String fab = scanner.nextLine();
                System.out.print("  Generacion: "); String gen = scanner.nextLine();
                nuevo = new Consola(nuevoId, nombre, costo, margen, fab, gen);
            } else if (tipo == 3) {
                System.out.println("  Subcategoria: 1. Audio  2. Hogar y Tecnologia");
                System.out.print("  Opcion: "); int sc = scanner.nextInt(); scanner.nextLine();
                String sub = (sc == 1) ? "Audio" : "Hogar y Tecnologia";
                System.out.print("  Descripcion: "); String desc = scanner.nextLine();
                nuevo = new Accesorio(nuevoId, nombre, costo, margen, sub, desc);
            } else { System.out.println("  Tipo invalido."); return; }
            gestor.agregarProducto(nuevo);
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] Dato invalido."); scanner.nextLine();
        } catch (ProductoExcepcion e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    private void ejecutarCU004() {
        System.out.println("\n--- CU004: Analizar Diferencia por Canal de Venta ---");
        gestor.listarProductos(null);
        System.out.print("  ID del producto (0 para volver): ");
        try {
            int id = scanner.nextInt();
            if (id != 0) gestor.analizarPorCanal(id);
        } catch (InputMismatchException e) {
            System.out.println("  [ERROR] ID invalido."); scanner.nextLine();
        } catch (ProductoExcepcion e) { System.out.println("  [ERROR] " + e.getMessage()); }
    }

    private int leerOpcion() {
        try { return scanner.nextInt(); }
        catch (InputMismatchException e) {
            System.out.println("  [ERROR] Ingrese un numero valido.");
            scanner.nextLine(); return -1;
        }
    }
}
