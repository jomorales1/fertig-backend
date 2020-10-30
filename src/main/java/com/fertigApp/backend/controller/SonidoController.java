package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.services.SonidoService;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SonidoController {

    private final SonidoService sonidoService;

    private final UsuarioService usuarioService;

    public SonidoController(SonidoService sonidoService, UsuarioService usuarioService) {
        this.sonidoService = sonidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping(path = "sounds/getSounds")
    public Iterable<Sonido> getAllSoundsByUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }

}
