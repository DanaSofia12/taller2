package com.museo.modelos;
import java.time.LocalDate;

public class Escultura extends ObraArte {
    private String estilo;
    private String material;

    public Escultura(String id, String titulo, String autor, double valor, LocalDate fechaEntrada, String estilo, String material) {
        super(id, titulo, autor, valor, fechaEntrada);
        this.estilo = estilo;
        this.material = material;
    }
}