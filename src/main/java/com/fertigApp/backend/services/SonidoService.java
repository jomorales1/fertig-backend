package com.fertigApp.backend.services;

import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.repository.SonidoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SonidoService {

    private final SonidoRepository sonidoRepository;

    public SonidoService(SonidoRepository sonidoRepository) {
        this.sonidoRepository = sonidoRepository;
    }

    public Iterable<Sonido> findAll() {
        return this.sonidoRepository.findAll();
    }

    public Optional<Sonido> findById(String id) {
        return this.sonidoRepository.findById(id);
    }

    public Sonido save(Sonido sonido) {
        return this.sonidoRepository.save(sonido);
    }

    public void deleteById(String id) {
        this.sonidoRepository.deleteById(id);
    }
}
