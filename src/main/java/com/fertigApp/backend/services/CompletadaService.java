package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.repository.CompletadaRepository;
import org.springframework.stereotype.Service;

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
}
