package com.museo.modelos;
import java.time.LocalDate;

public class Cuadro extends ObraArte {
    private String estilo;
    private String tecnica;

    public Cuadro(String id, String titulo, String autor, double valor, LocalDate fechaEntrada, String estilo, String tecnica) {
        super(id, titulo, autor, valor, fechaEntrada);
        this.estilo = estilo;
        this.tecnica = tecnica;
    }
}