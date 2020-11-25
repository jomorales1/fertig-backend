package com.fertigapp.backend.services;

import com.fertigapp.backend.model.Completada;
import com.fertigapp.backend.model.Rutina;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.CompletadaRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CompletadaService {

    private final CompletadaRepository completadaRepository;

    public CompletadaService(CompletadaRepository completadaRepository) {
        this.completadaRepository = completadaRepository;
    }

    public Completada save(Completada completada){
        return completadaRepository.save(completada);
    }

    public Completada findTopHechaByRutinaAndHecha(Rutina rutina, boolean hecha){
        return completadaRepository.findTopByRutinaCAndHecha(rutina, hecha);
    }

    public void deleteAllByRutina(Rutina rutina){
        this.completadaRepository.deleteAllByRutinaC(rutina);
    }

    public Iterable<OffsetDateTime> findFechasCompletadasByRutina(Rutina rutina){
        return this.completadaRepository.findFechasCompletadasByRutina(rutina);
    }

    public OffsetDateTime findMaxFechaCompletadaByRutina(Rutina rutina){
        return this.completadaRepository.findMaxFechaCompletadaByRutina(rutina);
    }

    public OffsetDateTime findFechaNoCompletadaByRutina(Rutina rutina){
        return this.completadaRepository.findFechaNoCompletadaByRutina(rutina);
    }

    public void deleteById(Integer id){
        this.completadaRepository.deleteById(id);
    }

    public Completada findMaxCompletada(Rutina rutina){
        return this.completadaRepository.findMaxCompletada(rutina);
    }

    public Integer countCompletadasBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario){
        return this.completadaRepository.countCompletadasBetween(inicio, fin, usuario);
    }

    public Integer countTiempoCompletadasBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario){
        return this.completadaRepository.countTiempoCompletadasBetween(inicio, fin, usuario);
    }
}
