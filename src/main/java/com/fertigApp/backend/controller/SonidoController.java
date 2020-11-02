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

    @PostMapping(path = "sounds/addPrefer/{id}")
    public @ResponseBody ResponseEntity<Void> addPrefer(@PathVariable String id){
        Preferido preferido = new Preferido();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        if(this.usuarioService.findById(userDetails.getUsername()).isPresent()){
            preferido.setUsuario(this.usuarioService.findById(userDetails.getUsername()).get());
        }
        preferido.setSonido(this.sonidoService.findById(id).get());
        this.preferidoService.add(preferido);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping(path = "sounds/getPrefers")
    public Iterable<Preferido> getPrefersByUsuario(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = this.usuarioService.findById(userDetails.getUsername()).get();
        return this.preferidoService.getByUsuario(usuario);
    }

    @GetMapping(path = "sounds/getSounds")
    public Iterable<Sonido> getAllSoundsByUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return null;
    }

    @DeleteMapping(path = "sounds/deletePrefer/{id}")
    public ResponseEntity<Void> deletePreferido(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!this.preferidoService.findById(id).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Usuario usuario = this.usuarioService.findById(userDetails.getUsername()).get();
        this.preferidoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
