package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.payload.response.SonidoResponse;
import com.fertigApp.backend.services.PreferidoService;
import com.fertigApp.backend.services.SonidoService;
import com.fertigApp.backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(path = "/sounds")
    public ResponseEntity<List<SonidoResponse>> getAllSounds() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Sonido> sonidos = (List<Sonido>) this.sonidoService.findAll();
        List<SonidoResponse> sonidoResponses = new ArrayList<>();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        for (Sonido sonido : sonidos) {
            SonidoResponse sonidoResponse = new SonidoResponse();
            sonidoResponse.setSonido(sonido.getId());
            sonidoResponse.setFavorite(this.preferidoService.findByUsuarioAndSonido(usuario, sonido).isPresent());
            sonidoResponses.add(sonidoResponse);
        }
        return ResponseEntity.ok(sonidoResponses);
    }

    @PostMapping(path = "/sound/add-favorite/{id}")
    public @ResponseBody ResponseEntity<Void> addFavorite(@PathVariable String id){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Sonido> optionalSonido = this.sonidoService.findById(id);
        if (optionalSonido.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Sonido sonido = optionalSonido.get();
        Preferido preferido = new Preferido();
        preferido.setUsuario(usuario);
        preferido.setSonido(sonido);
        this.preferidoService.save(preferido);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/sound/favorites")
    public ResponseEntity<List<Sonido>> getAllFavoritesByUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        List<Preferido> preferidos = (List<Preferido>) this.preferidoService.findByUsuario(usuario);
        List<Sonido> sonidos = new ArrayList<>();
        for (Preferido p: preferidos) {
            Optional<Sonido> optionalSonido = this.sonidoService.findById(p.getSonido().getId());
            sonidos.add(optionalSonido.orElse(new Sonido()));
        }
        return ResponseEntity.ok(sonidos);
    }

    @DeleteMapping(path = "/sound/delete-favorite/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Sonido> optionalSonido = this.sonidoService.findById(id);
        if (optionalSonido.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Usuario> optionalUsuario = this.usuarioService.findByUsuario(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Sonido sonido = optionalSonido.get();
        if (this.preferidoService.findByUsuarioAndSonido(usuario, sonido).isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        this.preferidoService.deleteAllByUsuarioAndSonido(usuario, sonido);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
