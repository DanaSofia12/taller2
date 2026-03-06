package com.museo.presentacion;

import com.museo.modelos.*;
import java.time.LocalDate;
import java.util.List;

public class AplicacionMuseo {
    
    public void iniciar() {
        System.out.println("=== SISTEMA DE GESTIÓN DEL MUSEO INICIADO ===\n");

        // 1. Instanciar el Gestor Central
        MuseoGestor museo = new MuseoGestor("Museo Nacional de Arte");

        // 2. Crear obras (Nota cómo simulamos una obra antigua para forzar el mantenimiento)
        Cuadro monaLisa = new Cuadro("C-01", "La Mona Lisa", "Da Vinci", 850000000.0, LocalDate.of(2015, 1, 1), "Renacimiento", "Óleo sobre tabla");
        Escultura david = new Escultura("E-01", "El David", "Miguel Ángel", 500000000.0, LocalDate.now(), "Renacimiento", "Mármol");

        museo.agregarObra(monaLisa);
        museo.agregarObra(david);

        System.out.println("-> Director consultando valoración total del museo...");
        System.out.printf("Valoración Total: $%.2f%n\n", museo.calcularValoracionTotal());

        System.out.println("-> Ejecutando proceso diario de revisión de restauraciones (Regla de 5 años)...");
        List<ObraArte> obrasEnviadas = museo.ejecutarProcesoDiarioRestauracion();
        
        for (ObraArte obra : obrasEnviadas) {
            System.out.println("ALERTA: La obra '" + obra.getTitulo() + "' ha sido enviada a restauración automática.");
        }

        // Validamos que 'El David' no se envió porque acaba de entrar hoy
        System.out.println("Estado de 'El David': " + david.getEstado());
        System.out.println("Estado de 'La Mona Lisa': " + monaLisa.getEstado());
    }
}