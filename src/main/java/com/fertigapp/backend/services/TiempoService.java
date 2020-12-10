package com.fertigapp.backend.services;

import com.fertigapp.backend.model.IdTiempo;
import com.fertigapp.backend.model.Tarea;
import com.fertigapp.backend.model.Tiempo;
import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.TiempoRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class TiempoService {

    private final TiempoRepository tiempoRepository;

    public TiempoService(TiempoRepository tiempoRepository) {
        this.tiempoRepository = tiempoRepository;
    }

    public Iterable<Tiempo> findAllByUsuarioAndTarea(Usuario usuario, Tarea tarea) {
        return this.tiempoRepository.findAllByUsuarioAndTarea(usuario, tarea);
    }

    public Tiempo save(Tiempo tiempo) {
        return this.tiempoRepository.save(tiempo);
    }

    public void deleteById(IdTiempo id) {
        this.tiempoRepository.deleteById(id);
    }

    public Integer countTiempoTareaBetween(OffsetDateTime inicio, OffsetDateTime fin, Usuario usuario){
        return this.tiempoRepository.countTiempoTareasBetween(inicio, fin, usuario);
    }

}
