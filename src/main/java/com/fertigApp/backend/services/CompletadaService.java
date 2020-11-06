package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.repository.CompletadaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public  Iterable<Completada> findHechaByRutina(Rutina rutina){
        return completadaRepository.findByRutinaCAndHecha(rutina, false);
    }

    public void deleteAllByRutina(Rutina rutina){
        this.completadaRepository.deleteAllByRutinaC(rutina);
    }

    public Iterable<LocalDateTime> findFechasCompletadasByRutina(Rutina rutina){
        return this.completadaRepository.findFechasCompletadasByRutina(rutina);
    }

    public LocalDateTime findMaxAjustadaCompletadasByRutina(Rutina rutina){
        return this.completadaRepository.findMaxAjustadaCompletadasByRutina(rutina);
    }

    public LocalDateTime findFechaNoCompletadaByRutina(Rutina rutina){
        return this.completadaRepository.findFechaNoCompletadaByRutina(rutina);
    }

    public void deleteById(Integer id){
        this.completadaRepository.deleteById(id);
    }

    public Completada findMaxCompletada(Rutina rutina){
        return this.completadaRepository.findMaxCompletada(rutina);
    }
}
