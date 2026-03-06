package com.museo.modelos;

import com.museo.enums.EstadoObra;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class ObraArte {
    private String idObra;
    private String titulo;
    private String autor;
    private double valoracionEconomica;
    private LocalDate fechaEntrada;
    private EstadoObra estado;
    
    private List<Restauracion> historialRestauraciones;
    private Queue<Cesion> colaCesiones; // Estructura FIFO para préstamos en espera

    public ObraArte(String idObra, String titulo, String autor, double valor, LocalDate fechaEntrada) {
        this.idObra = idObra;
        this.titulo = titulo;
        this.autor = autor;
        this.valoracionEconomica = valor;
        this.fechaEntrada = fechaEntrada;
        this.estado = EstadoObra.ALMACENADA;
        this.historialRestauraciones = new ArrayList<>();
        this.colaCesiones = new LinkedList<>();
    }

    // Regla de negocio: Restauración cada 5 años
    public boolean necesitaRestauracionRutinaria() {
        if (this.estado == EstadoObra.EN_RESTAURACION) return false;

        LocalDate fechaBaseCalculo = fechaEntrada;
        
        if (!historialRestauraciones.isEmpty()) {
            Restauracion ultima = historialRestauraciones.get(historialRestauraciones.size() - 1);
            if (ultima.getFechaFin() != null) {
                fechaBaseCalculo = ultima.getFechaFin();
            } else {
                return false; // Está en restauración actualmente
            }
        }

        long aniosTranscurridos = ChronoUnit.YEARS.between(fechaBaseCalculo, LocalDate.now());
        return aniosTranscurridos >= 5;
    }

    public void enviarARestauracion(String tipo, boolean esUrgentePorDanio) {
        this.estado = EstadoObra.EN_RESTAURACION;
        this.historialRestauraciones.add(new Restauracion(UUID.randomUUID().toString(), tipo, esUrgentePorDanio));
    }

    public void finalizarRestauracion() {
        if (!historialRestauraciones.isEmpty()) {
            Restauracion actual = historialRestauraciones.get(historialRestauraciones.size() - 1);
            actual.finalizar();
            this.estado = EstadoObra.ALMACENADA; // Vuelve al almacén tras restaurar
        }
    }

    public void programarCesion(Cesion cesion) {
        this.colaCesiones.offer(cesion); // Agrega a la cola de espera
    }

    public double getValoracionEconomica() { return valoracionEconomica; }
    public String getTitulo() { return titulo; }
    public EstadoObra getEstado() { return estado; }
    public List<Restauracion> getHistorialRestauraciones() { return historialRestauraciones; }
}