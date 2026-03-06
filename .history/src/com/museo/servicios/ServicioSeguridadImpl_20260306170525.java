package com.museo.servicios;

import com.museo.enums.RolUsuario;
import com.museo.interfaces.IServicioSeguridad;
import com.museo.modelos.Usuario;
import java.util.HashMap;
import java.util.Map;

public class ServicioSeguridadImpl implements IServicioSeguridad {
    private Map<String, Usuario> baseDatosUsuarios;

    public ServicioSeguridadImpl() {
        baseDatosUsuarios = new HashMap<>();
        // Quemamos unos usuarios en la "Base de Datos" para poder entrar al sistema
        baseDatosUsuarios.put("director", new Usuario("U-1", "director", "1234", RolUsuario.DIRECTOR));
        baseDatosUsuarios.put("restaurador", new Usuario("U-2", "restaurador", "1234", RolUsuario.RESTAURADOR_JEFE));
        baseDatosUsuarios.put("encargado", new Usuario("U-3", "encargado", "1234", RolUsuario.ENCARGADO_CATALOGO));
        baseDatosUsuarios.put("visitante", new Usuario("U-4", "visitante", "", RolUsuario.VISITANTE));
    }

    @Override
    public Usuario autenticar(String username, String password) {
        Usuario usuario = baseDatosUsuarios.get(username);
        // Validamos que el usuario exista y la contraseña coincida
        if (usuario != null && usuario.getPasswordHash().equals(password)) {
            return usuario;
        }
        return null;
    }

    @Override
    public boolean verificarPermiso(Usuario usuario, RolUsuario rolRequerido) {
        return usuario != null && usuario.getRol() == rolRequerido;
    }
}