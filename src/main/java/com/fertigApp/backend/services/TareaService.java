package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.repository.TareaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TareaService {

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

}
