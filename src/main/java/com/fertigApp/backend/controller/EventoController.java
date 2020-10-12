package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.repository.EventoRepository;
import com.fertigApp.backend.repository.UsuarioRepository;
import com.fertigApp.backend.requestModels.RequestEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/*
 * Clase responsable de manejar request de tipo GET, POST, PUT y DELETE para
 * la entidad "Evento".
 * */
@RestController
public class EventoController {

    private static final Logger LOGGER= LoggerFactory.getLogger(Completada.class);

    // Repositorio responsable del manejo de la tabla "evento" en la DB.
    @Autowired
    private EventoRepository eventoRepository;

    // Repositorio responsable del manejo de la tabla "usuario" en la DB.
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método GET para obtener del servidor una lista de todos los eventos
    // en la DB.
    @GetMapping(path="/events")
    public @ResponseBody Iterable<Evento> getAllEventos() {
        return this.eventoRepository.findAll();
    }

    // Método GET para obtener la lista de eventos de un usuario determinado.
    @GetMapping(path="/events/getEvents")
    public Iterable<Evento> getAllEventosByUsuario() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usuarioRepository.findById(userDetails.getUsername()).isPresent())
            return usuarioRepository.findById(userDetails.getUsername()).get().getEventos();
        else{
            LOGGER.info("User not found");
            return null;
        }
    }

    // Método GET para obtener un evento específico de un usuario por medio de su ID.
    @GetMapping(path="/events/getEvent/{id}")
    public Evento getEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user = userDetails.getUsername();

        if (this.eventoRepository.findById(id).isPresent()){
            Evento evento = this.eventoRepository.findById(id).get();
            if (evento.getUsuario().getUsuario().equals(user)) {
                System.out.println("Wrong user");
                return null;
            }
            return this.eventoRepository.findById(id).get();
        }
        LOGGER.info("Event not found");
        return null;

    }

    // Método PUT para actualizar un evento específico.
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

    // Método POST para agregar un evento a la DB.
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

    // Método DELETE para borrar un registro de la tabla "evento" en la DB.
    @DeleteMapping(path="/events/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Integer id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(this.eventoRepository.findById(id).isPresent()){
            Evento evento = this.eventoRepository.findById(id).get();
            if (! evento.getUsuario().getUsuario().equals(userDetails.getUsername()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            this.eventoRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
