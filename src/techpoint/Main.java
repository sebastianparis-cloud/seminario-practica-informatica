package techpoint;

import techpoint.menu.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TechPoint - Gestor de Rentabilidad   ");
        System.out.println("  TP3 - Prototipo Java con POO          ");
        System.out.println("  Entrega N 3 de 4                      ");
        System.out.println("========================================\n");
        MenuPrincipal menu = new MenuPrincipal();
        menu.ejecutar();
    }
}
