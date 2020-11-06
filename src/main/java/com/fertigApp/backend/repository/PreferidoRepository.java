package com.fertigApp.backend.repository;

import com.fertigApp.backend.model.IdPreferido;
import com.fertigApp.backend.model.Preferido;
import com.fertigApp.backend.model.Sonido;
import com.fertigApp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface PreferidoRepository extends CrudRepository<Preferido, Integer> {
    Optional<Preferido> findById(String id);
    Iterable<Preferido> findPreferidoByUsuario(Usuario usuario);

    Optional<Preferido> findByUsuarioAndSonido(Usuario usuario, Sonido sonido);

    @Transactional
    void deleteById(String id);

    @Transactional
    void deleteAllByUsuarioAndSonido(Usuario usuario, Sonido sonido);
}
