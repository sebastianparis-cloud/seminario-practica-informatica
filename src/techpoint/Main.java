package techpoint;

import techpoint.menu.MenuPrincipal;

/**
 * Main - punto de entrada del sistema TechPoint.
 *
 * Prototipo TP4: Gestor de Rentabilidad y Precios Dinamicos
 * Materia: Seminario de Practica Informatica - Universidad Siglo 21
 * Estudiante: Paris, Sebastian | Legajo: VINF07520
 * Profesor tutor Catedra B: Ana Carolina Ferreyra
 * Entrega N 4 de 4
 *
 * TP4 agrega sobre el TP3:
 *   - Patron MVC completo: paquete techpoint.controlador con 4 controladores
 *   - Persistencia real: toda operacion del menu persiste en MySQL via DAO
 *   - CU005: Gestionar Canales de Venta (ABM completo con CanalDAO)
 *   - Notebook: nueva subclase concreta (categoria Computacion)
 *   - Arreglo nativo double[]: complementa el ArrayList en GestorRentabilidad
 *   - Archivos: no aplica — justificacion en documento (datos persisten en MySQL)
 *
 * Repositorio GitHub: https://github.com/sebastianparis-cloud/seminario-practica-informatica
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TechPoint - Gestor de Rentabilidad   ");
        System.out.println("  Sistema de Gestion de Precios v1.0   ");
        System.out.println("  Precios dinamicos | Canal de venta    ");
        System.out.println("========================================");
        System.out.println();

        MenuPrincipal menu = new MenuPrincipal();
        menu.ejecutar();
    }
}
