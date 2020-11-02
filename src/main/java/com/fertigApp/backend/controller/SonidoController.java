package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.RequestSonido;
import com.fertigApp.backend.services.PreferidoService;
import com.fertigApp.backend.services.SonidoService;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SonidoController {

    private final SonidoService sonidoService;

    private final UsuarioService usuarioService;

    private final PreferidoService preferidoService;

    public SonidoController(SonidoService sonidoService, UsuarioService usuarioService, PreferidoService preferidoService) {
        this.sonidoService = sonidoService;
        this.usuarioService = usuarioService;
        this.preferidoService = preferidoService;
    }

    @PostMapping(path = "sounds/addSound")
    public @ResponseBody ResponseEntity<Void> addSound(@RequestBody RequestSonido requestSonido){
        Preferido preferido = new Preferido();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        if(this.usuarioService.findById(userDetails.getUsername()).isPresent()){
            preferido.setUsuario(this.usuarioService.findById(userDetails.getUsername()).get());
        }
        Sonido sonido = new Sonido();
        sonido.setId(requestSonido.getIdSonido());
        preferido.setSonido(this.sonidoService.save(sonido));
        this.preferidoService.add(preferido);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "sounds/getSounds")
    public List<Sonido> getAllSoundsByUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = this.usuarioService.findById(userDetails.getUsername()).get();
        List<Preferido> preferidos = (List<Preferido>) this.preferidoService.getByUsuario(usuario);
        List<Sonido> sonidos = new ArrayList<>();
        for(Preferido p: preferidos){
            sonidos.add(this.sonidoService.findById(p.getSonido().getId()).get());
        }
        return sonidos;
    }

    @DeleteMapping(path = "sounds/deleteSound/{id}")
    public ResponseEntity<Void> deleteSound(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.sonidoService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Usuario usuario = this.usuarioService.findById(userDetails.getUsername()).get();
        Sonido sonido = this.sonidoService.findById(id).get();
        Optional<Preferido> optionalPreferido = this.preferidoService.findByUsuarioAndSonido(usuario, sonido);
        if(!optionalPreferido.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        this.preferidoService.deleteAllByUsuarioAndSonido(usuario, sonido);
        this.sonidoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
