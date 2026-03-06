package com.museo.modelos;
import java.util.ArrayList;
import java.util.List;

public class MuseoGestor {
    private String nombre;
    private List<ObraArte> catalogoGeneral;

    public MuseoGestor(String nombre) {
        this.nombre = nombre;
        this.catalogoGeneral = new ArrayList<>();
    }

    public void agregarObra(ObraArte obra) {
        this.catalogoGeneral.add(obra);
    }

    public double calcularValoracionTotal() {
        return catalogoGeneral.stream()
                .mapToDouble(ObraArte::getValoracionEconomica)
                .sum();
    }

    public List<ObraArte> ejecutarProcesoDiarioRestauracion() {
        List<ObraArte> obrasARestaurar = new ArrayList<>();
        for (ObraArte obra : catalogoGeneral) {
            if (obra.necesitaRestauracionRutinaria()) {
                obra.enviarARestauracion("Mantenimiento rutinario (5 años)", false);
                obrasARestaurar.add(obra);
            }
        }
        return obrasARestaurar;
    }

    // --- MÉTODO PARA BUSCAR ---
    public ObraArte buscarObraPorId(String id) {
        return catalogoGeneral.stream()
                .filter(o -> o.getIdObra().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<ObraArte> getCatalogo() { return catalogoGeneral; }
}