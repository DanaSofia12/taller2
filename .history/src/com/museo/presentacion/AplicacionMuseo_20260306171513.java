package com.museo.presentacion;

import com.museo.enums.EstadoObra;
import com.museo.modelos.*;
import com.museo.servicios.ServicioSeguridadImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AplicacionMuseo {
    private Scanner scanner;
    private MuseoGestor museo;
    private ServicioSeguridadImpl seguridad;
    private Usuario usuarioActual;

    public AplicacionMuseo() {
        this.scanner = new Scanner(System.in);
        this.museo = new MuseoGestor("Museo Nacional de Arte");
        this.seguridad = new ServicioSeguridadImpl();
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        // Obra antigua para probar el mantenimiento automático de 5 años
        museo.agregarObra(new Cuadro("C-01", "La Noche Estrellada", "Van Gogh", 1000000, LocalDate.of(2010, 1, 1), "Postimpresionismo", "Óleo"));
        // Escultura reciente
        museo.agregarObra(new Escultura("E-01", "El Pensador", "Rodin", 500000, LocalDate.now(), "Realismo", "Bronce"));
    }

    public void iniciar() {
        boolean sistemaActivo = true;

        while (sistemaActivo) {
            System.out.println("\n=================================");
            System.out.println("  SISTEMA DE GESTIÓN DEL MUSEO");
            System.out.println("=================================");
            System.out.println("Por favor, inicie sesión.");
            System.out.println("(Usuarios: director, restaurador, encargado. Clave: 1234)");
            System.out.println("(Para visitantes ingrese usuario 'visitante' y clave vacía)");
            System.out.println("Escriba 'salir' en el usuario para apagar el sistema.");
            
            System.out.print("\nUsuario: ");
            String username = scanner.nextLine();
            
            if(username.equalsIgnoreCase("salir")) {
                sistemaActivo = false;
                break;
            }

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            usuarioActual = seguridad.autenticar(username, password);

            if (usuarioActual != null) {
                System.out.println("\n>>> Bienvenido, " + usuarioActual.getRol() + " <<<");
                enrutarMenuPorRol();
            } else {
                System.out.println("\n[ERROR] Credenciales incorrectas. Intente de nuevo.");
            }
        }
        System.out.println("Sistema apagado correctamente.");
    }

    // --- ENRUTAMIENTO DE SEGURIDAD ---
    private void enrutarMenuPorRol() {
        boolean sesionActiva = true;
        while (sesionActiva) {
            switch (usuarioActual.getRol()) {
                case DIRECTOR:
                    sesionActiva = menuDirector();
                    break;
                case ENCARGADO_CATALOGO:
                    sesionActiva = menuEncargado();
                    break;
                case RESTAURADOR_JEFE:
                    sesionActiva = menuRestaurador();
                    break;
                case VISITANTE:
                    sesionActiva = menuVisitante();
                    break;
            }
        }
    }

    // --- MENÚS ESPECÍFICOS POR ROL ---

    private boolean menuDirector() {
        System.out.println("\n-- PANEL DE DIRECTOR --");
        System.out.println("1. Ver Valoración Total del Museo");
        System.out.println("2. Ceder obra a otro museo (Préstamo)");
        System.out.println("0. Cerrar Sesión");
        System.out.print("Opción: ");
        
        String opc = scanner.nextLine();
        if (opc.equals("1")) {
            System.out.printf("La valoración total del catálogo es: $%.2f%n", museo.calcularValoracionTotal());
        } else if (opc.equals("2")) {
            System.out.print("ID de la obra a prestar: ");
            ObraArte obraPrestar = museo.buscarObraPorId(scanner.nextLine());
            
            if (obraPrestar != null) {
                System.out.print("Nombre del museo destino: ");
                MuseoExterno destino = new MuseoExterno("M-EXT", scanner.nextLine(), "Internacional");
                
                try {
                    System.out.print("Importe pagado por la cesión ($): ");
                    double importe = Double.parseDouble(scanner.nextLine());
                    
                    Cesion nuevaCesion = new Cesion(UUID.randomUUID().toString(), destino, LocalDate.now(), LocalDate.now().plusMonths(3), importe);
                    obraPrestar.programarCesion(nuevaCesion);
                    obraPrestar.enviarARestauracion("Preparación para envío (Cesión)", false); // Cambiamos el estado temporalmente
                    
                    System.out.println("Cesión programada exitosamente a " + destino.getNombre() + " por $" + importe);
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] El importe debe ser un valor numérico válido.");
                }
            } else {
                System.out.println("[ERROR] Obra no encontrada en el catálogo.");
            }
        } else if (opc.equals("0")) {
            return false;
        }
        return true;
    }

    private boolean menuEncargado() {
        System.out.println("\n-- PANEL DE CATÁLOGO --");
        System.out.println("1. Ver Catálogo Completo");
        System.out.println("2. Registrar nuevo Cuadro");
        System.out.println("0. Cerrar Sesión");
        System.out.print("Opción: ");

        String opc = scanner.nextLine();
        if (opc.equals("1")) {
            mostrarCatalogo();
        } else if (opc.equals("2")) {
            registrarCuadro();
        } else if (opc.equals("0")) {
            return false;
        }
        return true;
    }

    private boolean menuRestaurador() {
        System.out.println("\n-- PANEL DE RESTAURACIÓN --");
        System.out.println("1. Ejecutar revisión diaria (Automático 5 años)");
        System.out.println("2. Reportar daño urgente (Enviar a restauración)");
        System.out.println("3. Finalizar restauración de una obra");
        System.out.println("4. Ver estado de todas las obras");
        System.out.println("5. Consultar historial de restauraciones de una obra");
        System.out.println("0. Cerrar Sesión");
        System.out.print("Opción: ");

        String opc = scanner.nextLine();
        switch (opc) {
            case "1":
                List<ObraArte> enviadas = museo.ejecutarProcesoDiarioRestauracion();
                System.out.println("Proceso finalizado. Se enviaron " + enviadas.size() + " obras a mantenimiento rutinario.");
                break;
            case "2":
                mostrarCatalogo();
                System.out.print("\nID de la obra dañada: ");
                ObraArte obraDanada = museo.buscarObraPorId(scanner.nextLine());
                if(obraDanada != null && obraDanada.getEstado() != EstadoObra.EN_RESTAURACION) {
                    obraDanada.enviarARestauracion("Reparación por Daño Urgente", true);
                    System.out.println("Obra enviada a restauración inmediatamente.");
                } else {
                    System.out.println("[ERROR] Obra no encontrada o ya se encuentra en restauración.");
                }
                break;
            case "3":
                System.out.print("ID de la obra a dar de alta: ");
                ObraArte obraLista = museo.buscarObraPorId(scanner.nextLine());
                if(obraLista != null && obraLista.getEstado() == EstadoObra.EN_RESTAURACION) {
                    obraLista.finalizarRestauracion();
                    System.out.println("Restauración finalizada. La obra volvió al almacén.");
                } else {
                    System.out.println("[ERROR] La obra no está en restauración o no existe.");
                }
                break;
            case "4":
                mostrarCatalogo();
                break;
            case "5":
                System.out.print("Ingrese el ID de la obra: ");
                ObraArte obraHistorial = museo.buscarObraPorId(scanner.nextLine());
                if (obraHistorial != null) {
                    System.out.println("\n--- Historial de: " + obraHistorial.getTitulo() + " ---");
                    List<Restauracion> historial = obraHistorial.getHistorialRestauraciones();
                    if (historial.isEmpty()) {
                        System.out.println("Esta obra nunca ha sido restaurada.");
                    } else {
                        for (Restauracion r : historial) {
                            System.out.println(" - " + r.getDetalles());
                        }
                    }
                } else {
                    System.out.println("[ERROR] Obra no encontrada.");
                }
                break;
            case "0":
                return false;
        }
        return true;
    }

    private boolean menuVisitante() {
        System.out.println("\n-- MONITOR VESTÍBULO --");
        System.out.println("1. Consultar Obras por Salas");
        System.out.println("0. Salir del Monitor");
        System.out.print("Opción: ");
        
        if (scanner.nextLine().equals("1")) {
            System.out.println("\n=================================");
            System.out.println("   SALA 1: PINTURA CLÁSICA");
            System.out.println("=================================");
            museo.getCatalogo().stream()
                 .filter(o -> o instanceof Cuadro)
                 .forEach(o -> System.out.println(" -> " + o.getTitulo() + " (Autor: " + o.getAutor() + ")"));
                 
            System.out.println("\n=================================");
            System.out.println("   SALA 2: ESCULTURAS Y VOLUMEN");
            System.out.println("=================================");
            museo.getCatalogo().stream()
                 .filter(o -> o instanceof Escultura)
                 .forEach(o -> System.out.println(" -> " + o.getTitulo() + " (Autor: " + o.getAutor() + ")"));
        } else {
            return false;
        }
        return true;
    }

    // --- MÉTODOS AUXILIARES ---
    private void mostrarCatalogo() {
        for (ObraArte obra : museo.getCatalogo()) {
            System.out.printf("[%s] %s (Autor: %s) - Estado actual: %s%n", 
                obra.getIdObra(), obra.getTitulo(), obra.getAutor(), obra.getEstado());
        }
    }

    private void registrarCuadro() {
        try {
            System.out.print("Título: ");
            String titulo = scanner.nextLine();
            System.out.print("Autor: ");
            String autor = scanner.nextLine();
            System.out.print("Valoración Económica ($): ");
            double valor = Double.parseDouble(scanner.nextLine());
            System.out.print("Estilo: ");
            String estilo = scanner.nextLine();
            System.out.print("Técnica: ");
            String tecnica = scanner.nextLine();

            String id = "C-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Cuadro nuevo = new Cuadro(id, titulo, autor, valor, LocalDate.now(), estilo, tecnica);
            museo.agregarObra(nuevo);
            System.out.println("¡Éxito! Cuadro registrado en el catálogo con el ID: " + id);
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] La valoración debe ser un número válido sin símbolos de moneda. Registro cancelado.");
        }
    }
}