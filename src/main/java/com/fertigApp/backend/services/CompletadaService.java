package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.repository.CompletadaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompletadaService {

    private final CompletadaRepository completadaRepository;

    public CompletadaService(CompletadaRepository completadaRepository) {
        this.completadaRepository = completadaRepository;
    }

    public Optional<Completada> findById(Integer id){
        return completadaRepository.findById(id);
    }

    public Completada save(Completada completada){
        return completadaRepository.save(completada);
    }

    public Iterable<Completada> findByRutina(Rutina rutina){
        return completadaRepository.findByRutinaC(rutina);
    }

    public void deleteByRutina(Rutina rutina){
        List<Completada> completadaList = (List<Completada>) completadaRepository.findByRutinaC(rutina);
        for(Completada completada : completadaList){
            completadaRepository.deleteById(completada.getId());
        }
    }
}
