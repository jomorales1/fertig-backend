package com.fertigapp.backend.controller;

import com.fertigapp.backend.firebase.NotificationSystem;
import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.payload.response.EventoRepeticionesResponse;
import com.fertigapp.backend.payload.response.MessageResponse;
import com.fertigapp.backend.payload.response.RecurrenteResponse;
import com.fertigapp.backend.requestmodels.RequestEvento;
import com.fertigapp.backend.services.EventoService;
import com.fertigapp.backend.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Evento".
 * */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class EventoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);

    private static final String EV_NO_ENCONTRADO = "Evento no encontrado";
    private static final String EV_NO_PERTENECE = "El evento no pertenece al usuario";

    // Repositorio responsable del manejo de la tabla "evento" en la DB.
    private final EventoService eventoService;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    private final UsuarioService usuarioService;

    private final NotificationSystem notificationSystem;

    public EventoController(EventoService eventoService, UsuarioService usuarioService, NotificationSystem notificationSystem) {
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
        this.notificationSystem = notificationSystem;
    }

    // Método GET para obtener del servidor una lista de todos los eventos
    // en la DB.
    @GetMapping(path="/events")
    public @ResponseBody Iterable<Evento> getAllEventos() {
        return this.eventoService.findAll();
    }

    // Método GET para obtener la lista de eventos de un usuario determinado.
    @GetMapping(path="/event/events")
    public ResponseEntity<Iterable<RecurrenteResponse>> getAllEventosByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        List<RecurrenteResponse> eventos = new LinkedList<>();
        for(Evento evento : this.eventoService.findByUsuario(usuario)){
            eventos.add(new RecurrenteResponse(evento));
        }
        return ResponseEntity.ok().body(eventos);
    }

    @GetMapping(path="/event/events-and-repetitions")
    public ResponseEntity<Iterable<EventoRepeticionesResponse>> getAllEventosRepeticionesByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        List<EventoRepeticionesResponse> eventos = new LinkedList<>();
        for(Evento evento : eventoService.findByUsuario(usuario)){
            eventos.add(new EventoRepeticionesResponse(evento));
        }
        return ResponseEntity.ok().body(eventos);
    }

    // Método GET para obtener un evento específico de un usuario por medio de su ID.
    @GetMapping(path="/event/{id}")
    public ResponseEntity<Evento> getEvento(@PathVariable Integer id) {
        Optional<Evento> optionalEvento = this.eventoService.findById(id);
        if (optionalEvento.isEmpty()) {
            LOGGER.info(EV_NO_ENCONTRADO);
            return ResponseEntity.badRequest().body(null);
        }
        Evento evento = optionalEvento.get();
        return ResponseEntity.ok(evento);
    }

    // Método PUT para actualizar un evento específico.
    @PutMapping(path="/event/update/{id}")
    public ResponseEntity<Evento> replaceEvento(@PathVariable Integer id, @RequestBody RequestEvento event) {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        Optional<Evento> optionalEvento = eventoService.findById(id);
        if (optionalEvento.isEmpty()) {
            LOGGER.info(EV_NO_ENCONTRADO);
            return ResponseEntity.badRequest().body(null);
        }
        String username = userDetails.getUsername();
        Evento evento = optionalEvento.get();
        if (!evento.getUsuario().getUsuario().equals(username)) {
            LOGGER.info(EV_NO_PERTENECE);
            return ResponseEntity.badRequest().body(null);
        }
        evento.setNombre(event.getNombre());
        evento.setDescripcion(event.getDescripcion());
        evento.setPrioridad(event.getPrioridad());
        evento.setEtiqueta(event.getEtiqueta());
        evento.setDuracion(event.getDuracion());
        evento.setFechaInicio(event.getFechaInicio());
        evento.setFechaFin(event.getFechaFin());
        evento.setRecurrencia(event.getRecurrencia());
        evento.setRecordatorio(event.getRecordatorio());
        this.eventoService.save(evento);
        LOGGER.info("Evento actualizado");
        return ResponseEntity.ok().body(evento);
    }

    // Método POST para agregar un evento a la DB.
    @PostMapping(path="/event/add")
    public @ResponseBody ResponseEntity<Void> addNewEvento(@RequestBody RequestEvento requestEvento) {
        Evento evento = new Evento();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Usuario> optUsuario = usuarioService.findById(userDetails.getUsername());

        evento.setUsuario(optUsuario.orElse(null));
        evento.setNombre(requestEvento.getNombre());
        evento.setDescripcion(requestEvento.getDescripcion());
        evento.setPrioridad(requestEvento.getPrioridad());
        evento.setEtiqueta(requestEvento.getEtiqueta());
        evento.setDuracion(requestEvento.getDuracion());
        evento.setFechaInicio(requestEvento.getFechaInicio());
        evento.setFechaFin(requestEvento.getFechaFin());
        evento.setRecurrencia(requestEvento.getRecurrencia());
        evento.setRecordatorio(requestEvento.getRecordatorio());
        Evento savedEvent = this.eventoService.save(evento);
        if (savedEvent.getRecordatorio() != null) {
            this.notificationSystem.scheduleEventNotification(userDetails.getUsername(), savedEvent.getId());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Método DELETE para borrar un registro de la tabla "evento" en la DB.
    @DeleteMapping(path="/event/delete/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Evento> optionalEvento = this.eventoService.findById(id);
        if (optionalEvento.isEmpty()) {
            LOGGER.info(EV_NO_ENCONTRADO);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String username = userDetails.getUsername();
        Evento evento = optionalEvento.get();
        if (!evento.getUsuario().getUsuario().equals(username)) {
            LOGGER.info(EV_NO_PERTENECE);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        this.eventoService.deleteById(id);
        this.notificationSystem.cancelScheduledEventNotification(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/event/{id}/copy")
    public ResponseEntity<MessageResponse> copyEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Evento> optionalEvento = this.eventoService.findById(id);
        if (optionalEvento.isEmpty()) {
            LOGGER.info(EV_NO_ENCONTRADO);
            return ResponseEntity.badRequest().body(new MessageResponse(EV_NO_ENCONTRADO));
        }
        Optional<Usuario> optionalUsuario = this.usuarioService.findById(userDetails.getUsername());
        Usuario usuario = optionalUsuario.orElse(new Usuario());
        Evento evento = optionalEvento.get();
        if (evento.getUsuario().getUsuario().equals(userDetails.getUsername())) {
            LOGGER.info("El evento ya pertenece al usuario");
            return ResponseEntity.badRequest().body(new MessageResponse("Error: el evento ya pertenece al usuario"));
        }
        Evento copy = new Evento();
        copy.setUsuario(usuario);
        copy.setNombre(evento.getNombre());
        copy.setDuracion(evento.getDuracion());
        copy.setDescripcion(evento.getDescripcion());
        copy.setPrioridad(evento.getPrioridad());
        copy.setFechaInicio(evento.getFechaInicio());
        copy.setFechaFin(evento.getFechaFin());
        copy.setRecurrencia(evento.getRecurrencia());
        copy.setEtiqueta(evento.getEtiqueta());
        copy.setRecordatorio(evento.getRecordatorio());
        Evento savedEvent = this.eventoService.save(copy);
        if (savedEvent.getRecordatorio() != null) {
            this.notificationSystem.scheduleEventNotification(userDetails.getUsername(), savedEvent.getId());
        }
        return ResponseEntity.ok(new MessageResponse("Evento copiado"));
    }

}
