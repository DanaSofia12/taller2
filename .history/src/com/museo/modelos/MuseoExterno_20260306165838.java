// MuseoExterno.java
package com.museo.modelos;

public class MuseoExterno {
    private String idMuseo;
    private String nombre;
    private String pais;

    public MuseoExterno(String idMuseo, String nombre, String pais) {
        this.idMuseo = idMuseo;
        this.nombre = nombre;
        this.pais = pais;
    }
    public String getNombre() { return nombre; }
}