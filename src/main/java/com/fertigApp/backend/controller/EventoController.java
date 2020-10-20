package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.requestModels.RequestEvento;
import com.fertigApp.backend.services.EventoService;
import com.fertigApp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Level;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Evento".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class EventoController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "evento" en la DB.
    private final EventoService eventoService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    public EventoController(EventoService eventoService, UsuarioService usuarioService) {
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
    }

    // Método GET para obtener del servidor una lista de todos los eventos
    // en la DB.
    @GetMapping(path="/events")
    public @ResponseBody Iterable<Evento> getAllEventos() {
        return this.eventoService.findAll();
    }

    // Método GET para obtener la lista de eventos de un usuario determinado.
    @GetMapping(path="/events/getEvents")
    public Iterable<Evento> getAllEventosByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optUsuario =usuarioService.findById(userDetails.getUsername());
        return optUsuario.map(eventoService::findByUsuario).orElse(null);
    }

    // Método GET para obtener un evento específico de un usuario por medio de su ID.
    @GetMapping(path="/events/getEvent/{id}")
    public Evento getEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();

        Optional<Evento> optEvento = this.eventoService.findById(id);
        if (optEvento.isPresent()){
            if (!optEvento.get().getUsuario().getUsuario().equals(user)) {
                LOGGER.info("Wrong user");
                return null;
            }
            return optEvento.get();
        }
        LOGGER.info("Event not found");
        return null;
    }

    // Método PUT para actualizar un evento específico.
    @PutMapping(path="/events/updateEvent/{id}")
    public ResponseEntity<Evento> replaceEvento(@PathVariable Integer id, @RequestBody RequestEvento event) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        Optional<Evento> optionalEvento = eventoService.findById(id);
        Optional<Usuario> optionalUsuario = usuarioService.findByUsuario(userDetails.getUsername());
        if(optionalEvento.isPresent() && optionalUsuario.isPresent()){
            Evento evento = optionalEvento.get();

            evento.setUsuario(optionalUsuario.get());
            evento.setNombre(event.getNombre());
            evento.setDescripcion(event.getDescripcion());
            evento.setPrioridad(event.getPrioridad());
            evento.setEtiqueta(event.getEtiqueta());
            evento.setEstimacion(event.getEstimacion());
            evento.setFechaInicio(event.getFechaInicio());
            evento.setFechaFin(event.getFechaFin());
            evento.setRecurrencia(event.getRecurrencia());
            evento.setRecordatorio(event.getRecordatorio());
            this.eventoService.save(evento);
            LOGGER.info("Event updated");
            return ResponseEntity.ok().body(evento);
        } else {
            LOGGER.info("Event not found");
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Método POST para agregar un evento a la DB.
    @PostMapping(path="/events/addEvent")
    public @ResponseBody ResponseEntity<Void> addNewEvento(@RequestBody RequestEvento requestEvento) {
        Evento evento = new Evento();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.logging.Logger.getGlobal().log(Level.INFO,principal.toString());
        UserDetails userDetails = (UserDetails) principal;
        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());

        evento.setUsuario(optUsuario.orElse(null));
        evento.setNombre(requestEvento.getNombre());
        evento.setDescripcion(requestEvento.getDescripcion());
        evento.setPrioridad(requestEvento.getPrioridad());
        evento.setEtiqueta(requestEvento.getEtiqueta());
        if (requestEvento.getEstimacion() != null)
            evento.setEstimacion(requestEvento.getEstimacion());
        evento.setFechaInicio(requestEvento.getFechaInicio());
        evento.setFechaFin(requestEvento.getFechaFin());
        evento.setRecurrencia(requestEvento.getRecurrencia());
        if (requestEvento.getRecordatorio() != null)
            evento.setRecordatorio(requestEvento.getRecordatorio());
        this.eventoService.save(evento);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Método DELETE para borrar un registro de la tabla "evento" en la DB.
    @DeleteMapping(path="/events/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Evento> optEvento = this.eventoService.findById(id);
        if(optEvento.isPresent()){
            if (!optEvento.get().getUsuario().getUsuario().equals(userDetails.getUsername()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            this.eventoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
