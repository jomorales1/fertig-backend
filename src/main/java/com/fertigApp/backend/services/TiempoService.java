package com.fertigApp.backend.services;

import com.fertigApp.backend.model.IdTiempo;
import com.fertigApp.backend.model.Tarea;
import com.fertigApp.backend.model.Tiempo;
import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.TiempoRepository;
import org.springframework.stereotype.Service;

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

}
