package com.fertigapp.backend.services;

import com.fertigapp.backend.model.FranjaActiva;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.FranjaActivaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FranjaActivaService {

    private final FranjaActivaRepository franjaActivaRepository;

    public FranjaActivaService(FranjaActivaRepository franjaActivaRepository) {
        this.franjaActivaRepository = franjaActivaRepository;
    }

    public FranjaActiva save(FranjaActiva franjaActiva){
        return franjaActivaRepository.save(franjaActiva);
    }

    public Optional<FranjaActiva> findById(int id){
        return franjaActivaRepository.findById(id);
    }

    public Iterable<FranjaActiva> findByUser(Usuario usuario){
        return franjaActivaRepository.findAllByUsuarioFL(usuario);
    }

    public Optional<FranjaActiva> findByUserAndDay(Usuario usuario, int day){
        return franjaActivaRepository.findAllByUsuarioFLAndDay(usuario, day);
    }

    public void deleteById(int id){
        franjaActivaRepository.deleteById(id);
    }

    public void deleteByUser(Usuario usuario){
        franjaActivaRepository.deleteAllByUsuarioFL(usuario);
    }
}
