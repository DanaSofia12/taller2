package com.museo.modelos;
import java.time.LocalDate;

public class Restauracion {
    private String idRestauracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipo;
    private boolean porDanio;

    public Restauracion(String idRestauracion, String tipo, boolean porDanio) {
        this.idRestauracion = idRestauracion;
        this.fechaInicio = LocalDate.now(); // Inicia hoy
        this.tipo = tipo;
        this.porDanio = porDanio;
    }

    public void finalizar() {
        this.fechaFin = LocalDate.now();
    }

    public LocalDate getFechaFin() { return fechaFin; }
    public String getDetalles() {
        return String.format("[%s] Tipo: %s | Finalizada: %s", fechaInicio, tipo, (fechaFin != null ? fechaFin : "En proceso"));
    }
}