package com.museo.modelos;
import com.museo.enums.RolUsuario;

public class Usuario {
    private String idUsuario;
    private String username;
    private String passwordHash;

    public Usuario(String idUsuario, String username, String passwordHash, RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public RolUsuario getRol() { return rol; }
    public String getUsername() { return username; }
}