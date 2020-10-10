package com.fertigApp.backend.auth.configuration;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetailsManager userDetailsManager;

    //Modificación del usuerDetailsManger.
    @Bean
    public UserDetailsManager userDetailsManager() {
        userDetailsManager = new InMemoryUserDetailsManager(); //Creamos un nuevo InMemoryUserDetailsManager

        //Itermaos sobre los usuarios en el repositorio y los agregamos al userDetailsManager
        for (Usuario usuario: usuarioRepository.findAll() ){
            UserDetails user = User.builder().username(usuario.getUsuario()).password(usuario.getPassword()).
                    roles("USER").build();
            userDetailsManager.createUser(user);
        }

        //Retorno del nuevo userDetailsManager
        return userDetailsManager;
    }

    //Esta función llama a la anterior.
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsManager;
    }

    @Override
    protected void configure( AuthenticationManagerBuilder builder ) throws Exception{
        builder.userDetailsService( userDetailsService( ) ).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        http.authorizeRequests()
//                .antMatchers("/usuario/**").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().disable();

        http.csrf().disable(); //Desahibilitación de csfr por ser innecesario.
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
