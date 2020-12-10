package com.fertigapp.backend.services;

import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Iterable<Evento> findAll(){
        return eventoRepository.findAll();
    }

    public Optional<Evento> findById(Integer id){
        return  eventoRepository.findById(id);
    }

    public Evento save(Evento evento){
        return eventoRepository.save(evento);
    }

    public void deleteById(Integer id){
        eventoRepository.deleteById(id);
    }

    public Iterable<Evento> findByUsuario(Usuario usuario){
        return eventoRepository.findByUsuarioE(usuario);
    }

}
