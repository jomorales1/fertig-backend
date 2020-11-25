package com.fertigapp.backend.services;

import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TareaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TareaService.class);

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public Iterable<Tarea> findAll(){
        return tareaRepository.findAll();
    }

    public Optional<Tarea> findById(Integer id){
        return tareaRepository.findById(id);
    }

    public Tarea save(Tarea tarea){
        return tareaRepository.save(tarea);
    }

    public void deleteById(Integer id){
        tareaRepository.deleteById(id);
    }

    public Integer countTareasBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario){
        return this.tareaRepository.countTareasBetween(inicio, fin, usuario);
    }
}
