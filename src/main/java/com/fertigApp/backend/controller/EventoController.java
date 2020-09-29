package com.fertigApp.backend.controller;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventoController {
    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping(path="/events")
    public @ResponseBody Iterable<Evento> getAllEventos() {
        return this.eventoRepository.findAll();
    }

    @GetMapping(path="/events/{user}")
    public Iterable<Evento> getAllEventosByUsuario() {
        return null;
    }

    @GetMapping(path="/events/{id}")
    public Evento getEvento(@PathVariable Integer id) {
        return this.eventoRepository.findById(id).get();
    }

    @PutMapping(path="/events/{id}")
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

    @PostMapping(path="/events")
    public @ResponseBody String addNewEvento(@RequestBody Evento evento) {
        this.eventoRepository.save(evento);
        return "Saved";
    }

    @DeleteMapping(path="/events/{id}")
    public void deleteEvento(@RequestParam Integer id) {
        this.eventoRepository.deleteById(id);
    }
}
