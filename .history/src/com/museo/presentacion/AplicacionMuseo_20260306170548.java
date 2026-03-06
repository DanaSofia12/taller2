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
        this.museo = new MuseoGestor("Museo de Arte Moderno");
        this.seguridad = new ServicioSeguridadImpl();
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        // Agregamos un cuadro viejo para probar la alerta de 5 años
        museo.agregarObra(new Cuadro("C-01", "La Noche Estrellada", "Van Gogh", 1000000, LocalDate.of(2010, 1, 1), "Postimpresionismo", "Óleo"));
    }

    public void iniciar() {
        boolean sistemaActivo = true;

        while (sistemaActivo) {
            System.out.println("\n=================================");
            System.out.println("  SISTEMA DE GESTIÓN DEL MUSEO");
            System.out.println("=================================");
            System.out.println("Por favor, inicie sesión.");
            System.out.println("(Usuarios de prueba: director, restaurador, encargado. Clave: 1234)");
            System.out.println("(Para visitantes ingrese usuario 'visitante' y deje la clave vacía)");
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
        System.out.println("Sistema apagado.");
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
        System.out.println("0. Cerrar Sesión");
        System.out.print("Opción: ");
        
        String opc = scanner.nextLine();
        if (opc.equals("1")) {
            System.out.printf("La valoración total es: $%.2f%n", museo.calcularValoracionTotal());
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
        System.out.println("4. Ver estado de obras");
        System.out.println("0. Cerrar Sesión");
        System.out.print("Opción: ");

        String opc = scanner.nextLine();
        switch (opc) {
            case "1":
                List<ObraArte> enviadas = museo.ejecutarProcesoDiarioRestauracion();
                System.out.println("Se enviaron " + enviadas.size() + " obras a mantenimiento rutinario.");
                break;
            case "2":
                mostrarCatalogo();
                System.out.print("ID de la obra dañada: ");
                ObraArte obraDanada = museo.buscarObraPorId(scanner.nextLine());
                if(obraDanada != null) {
                    obraDanada.enviarARestauracion("Reparación por Daño Urgente", true);
                    System.out.println("Obra enviada a restauración.");
                } else System.out.println("Obra no encontrada.");
                break;
            case "3":
                System.out.print("ID de la obra a dar de alta: ");
                ObraArte obraLista = museo.buscarObraPorId(scanner.nextLine());
                if(obraLista != null && obraLista.getEstado() == EstadoObra.EN_RESTAURACION) {
                    obraLista.finalizarRestauracion();
                    System.out.println("Restauración finalizada. La obra volvió al almacén.");
                } else System.out.println("La obra no está en restauración.");
                break;
            case "4":
                mostrarCatalogo();
                break;
            case "0":
                return false;
        }
        return true;
    }

    private boolean menuVisitante() {
        System.out.println("\n-- MONITOR VESTÍBULO --");
        System.out.println("1. Consultar Obras");
        System.out.println("0. Salir del Monitor");
        System.out.print("Opción: ");
        
        if (scanner.nextLine().equals("1")) {
            System.out.println("\nObras disponibles en el museo:");
            mostrarCatalogo();
        } else {
            return false; // Sale de la sesión
        }
        return true;
    }

    // --- MÉTODOS AUXILIARES ---
    private void mostrarCatalogo() {
        for (ObraArte obra : museo.getCatalogo()) {
            System.out.printf("[%s] %s (Autor: %s) - Estado: %s%n", 
                obra.getIdObra(), obra.getTitulo(), obra.getAutor(), obra.getEstado());
        }
    }

    private void registrarCuadro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Valoración ($): ");
        double valor = Double.parseDouble(scanner.nextLine());
        System.out.print("Estilo: ");
        String estilo = scanner.nextLine();
        System.out.print("Técnica: ");
        String tecnica = scanner.nextLine();

        String id = "C-" + UUID.randomUUID().toString().substring(0, 4);
        Cuadro nuevo = new Cuadro(id, titulo, autor, valor, LocalDate.now(), estilo, tecnica);
        museo.agregarObra(nuevo);
        System.out.println("Cuadro registrado exitosamente con ID: " + id);
    }
}