package com.fertigapp.backend.auth.services;

import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/*
 * Implementacion de UserDetailsService para buscar usuarios en la base de datos y
 * a partir de ellos retornar un userDetails
 * */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        Usuario user = userRepository.findByUsuario(username).orElseThrow(()->new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }
}
