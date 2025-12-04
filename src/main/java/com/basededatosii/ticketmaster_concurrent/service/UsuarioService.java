package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Usuario;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);
    Usuario obtenerUsuarioPorId(Long id);
    Usuario obtenerUsuarioPorCorreo(String correo);
    List<Usuario> listarUsuarios();
}