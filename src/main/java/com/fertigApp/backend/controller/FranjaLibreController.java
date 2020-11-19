package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.FranjaLibre;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.services.FranjaLibreService;
import com.fertigApp.backend.services.TareaDeUsuarioService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "FranjaLibre".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FranjaLibreController {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FranjaLibre.class);

    // Repositorio responsable del manejo de la tabla "tarea" en la DB.
    private final TareaService tareaService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    // Repositorio responsable del manejo de la tabla "Tarea" en la DB.
    private final TareaDeUsuarioService tareaDeUsuarioService;

    private final FranjaLibreService franjaLibreService;

    public FranjaLibreController(TareaService tareaService, UsuarioService usuarioService, TareaDeUsuarioService tareaDeUsuarioService, FranjaLibreService franjaLibreService) {
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.tareaDeUsuarioService = tareaDeUsuarioService;
        this.franjaLibreService = franjaLibreService;
    }

    @GetMapping(path="/")
    public ResponseEntity<List<FranjaLibre>> getAllFranjasLibresByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(null);
        List<FranjaLibre> franjaLibres = (List<FranjaLibre>) this.franjaLibreService.findByUser(usuario);
        return ResponseEntity.ok(franjaLibres);
    }
}
