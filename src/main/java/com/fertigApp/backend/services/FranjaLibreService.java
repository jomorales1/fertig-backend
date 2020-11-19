package com.fertigApp.backend.services;

import com.fertigApp.backend.model.FranjaLibre;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.FranjaLibreRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FranjaLibreService {

    private final FranjaLibreRepository franjaLibreRepository;

    public FranjaLibreService(FranjaLibreRepository franjaLibreRepository) {
        this.franjaLibreRepository = franjaLibreRepository;
    }

    public FranjaLibre save(FranjaLibre franjaLibre){
        return franjaLibreRepository.save(franjaLibre);
    }

    public Optional<FranjaLibre> findById(int id){
        return franjaLibreRepository.findById(id);
    }

    public Iterable<FranjaLibre> findByUser(Usuario usuario){
        return franjaLibreRepository.findAllByUsuarioFL(usuario);
    }

    public Iterable<FranjaLibre> findByUserAndDay(Usuario usuario, int day){
        return franjaLibreRepository.findAllByUsuarioFLAAndDay(usuario, day);
    }

    public void deleteById(int id){
        franjaLibreRepository.deleteById(id);
    }

    public void deleteByUser(Usuario usuario){
        franjaLibreRepository.deleteByUsuarioFL(usuario);
    }
}
