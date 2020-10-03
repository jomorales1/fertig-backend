package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.repository.EventoRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventoController {
    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(path="/events")
    public @ResponseBody Iterable<Evento> getAllEventos() {
        return this.eventoRepository.findAll();
    }

    @GetMapping(path="/events/getEvents")
    public Iterable<Evento> getAllEventosByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return usuarioRepository.findById(userDetails.getUsername()).get().getEventos();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("User not found");
            return null;
        }
    }

    @GetMapping(path="/events/getEvent/{id}")
    public Evento getEvento(@PathVariable String user, @PathVariable Integer id) {
        try {
            Evento evento = this.eventoRepository.findById(id).get();
            if (evento.getUsuario().getUsuario() != user) {
                System.out.println("Wrong user");
                return null;
            }
            return this.eventoRepository.findById(id).get();
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Event not found");
            return null;
        }
    }

    @PutMapping(path="/events/updateEvent/{id}")
    public Evento replaceEvento(@PathVariable Integer id, @RequestBody Evento event) {
        return this.eventoRepository.findById(id)
                .map(evento -> {
                    evento = event;
                    this.eventoRepository.save(evento);
                    return evento;
                })
                .orElseGet(() -> {
                    this.eventoRepository.save(event);
                    return event;
                });
    }

    @PostMapping(path="/events/addEvent")
    public @ResponseBody ResponseEntity<Void> addNewEvento(@RequestBody RequestEvento requestEvento) {
        // Missing check information process
        Evento evento = new Evento();
        if (usuarioRepository.findById(requestEvento.getUsuario()).isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        evento.setUsuario(usuarioRepository.findById(requestEvento.getUsuario()).get());
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
        this.eventoRepository.save(evento);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(path="/events/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Evento evento = this.eventoRepository.findById(id).get();
        if (evento.getUsuario().getUsuario() != userDetails.getUsername())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        this.eventoRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
