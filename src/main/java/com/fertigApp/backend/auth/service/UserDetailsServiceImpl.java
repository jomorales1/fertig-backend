//package com.fertigApp.backend.auth.service;
//
//import com.fertigApp.backend.auth.model.UserDetailsImpl;
//import com.fertigApp.backend.model.Usuario;
//import com.fertigApp.backend.service.UsuarioService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service("userDetailsService")
//public class UserDetailsServiceImpl implements UserDetailsService{
//
//    private UsuarioService userService;
//
//    public UserDetailsServiceImpl (UsuarioService userService){
//        this.userService = userService;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Usuario usuario = userService.findByUsuario(username);
//        if (usuario == null){
//            throw new UsernameNotFoundException("");
//        }
//        return new UserDetailsImpl(usuario);
//    }
//}
