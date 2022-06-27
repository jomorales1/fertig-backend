package com.fertigapp.backend.repository;

import com.fertigapp.backend.model.PasswordResetToken;
import com.fertigapp.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findAllByUserOrderByExpiryDateDesc(Usuario user);
}
