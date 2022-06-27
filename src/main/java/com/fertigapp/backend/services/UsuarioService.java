package com.fertigapp.backend.services;

import com.fertigapp.backend.model.PasswordResetToken;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.PasswordTokenRepository;
import com.fertigapp.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordTokenRepository passwordTokenRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordTokenRepository passwordTokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordTokenRepository = passwordTokenRepository;
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

    public void createPasswordResetToken(Usuario usuario, String token) {
        PasswordResetToken userToken = new PasswordResetToken(token, usuario, OffsetDateTime.now().plusDays(1));
        this.passwordTokenRepository.save(userToken);
    }

    public Optional<PasswordResetToken> getPasswordResetToken(String token) {
        return this.passwordTokenRepository.findByToken(token);
    }

    public Optional<Usuario> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).orElse(new PasswordResetToken()).getUser());
    }

}
