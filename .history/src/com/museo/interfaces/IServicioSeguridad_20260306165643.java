package com.museo.interfaces;
import com.museo.enums.RolUsuario;
import com.museo.modelos.Usuario;

public interface IServicioSeguridad {
    Usuario autenticar(String username, String password);
    boolean verificarPermiso(Usuario usuario, RolUsuario rolRequerido);
}