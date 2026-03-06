// Cesion.java
package com.museo.modelos;
import java.time.LocalDate;

public class Cesion {
    private String idCesion;
    private MuseoExterno destino;
    private LocalDate fechaInicioEstimada;
    private LocalDate fechaFinEstimada;
    private double importePagado;
    private boolean activa;

    public Cesion(String idCesion, MuseoExterno destino, LocalDate inicio, LocalDate fin, double importe) {
        this.idCesion = idCesion;
        this.destino = destino;
        this.fechaInicioEstimada = inicio;
        this.fechaFinEstimada = fin;
        this.importePagado = importe;
        this.activa = false;
    }

    public void iniciar() { this.activa = true; }
    public void finalizar() { this.activa = false; }
    public MuseoExterno getDestino() { return destino; }
}