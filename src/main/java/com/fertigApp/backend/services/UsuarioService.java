package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;


    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Iterable<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Iterable<Usuario> findAllByUsuario(String usuario){
        return usuarioRepository.findAllByUsuario(usuario);
    }

    public Optional<Usuario> findById(String id){
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByUsuario(String usuario){
        return usuarioRepository.findByUsuario(usuario);
    }

    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public boolean existsById(String id){
        return usuarioRepository.existsById(id);
    }

    public boolean existsByCorreo(String correo){
        return usuarioRepository.existsByCorreo(correo);
    }

    public Usuario findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public void deleteById(String id){
        usuarioRepository.deleteById(id);
    }

}
