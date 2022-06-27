package com.fertigapp.backend.auth.configuration;

import com.fertigapp.backend.auth.jwt.AuthEntryPointJwt;
import com.fertigapp.backend.auth.jwt.AuthTokenFilter;
import com.fertigapp.backend.auth.services.UserDetailsServiceImpl;
import com.fertigapp.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;


    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    //URL's de los recursos públicos (no requieren Token)
    private static final String[] publicResources = new String[]
            {
                    "/oauth/token",
                    "/oauth/authorize**",
                    "/user/add",
                    "/user/reset-password",
                    "/user/save-password",
                    "/sign-in",
                    "/login/oauth2/code/google",
                    "/login/oauth2/code/facebook"
            };
    //configuración de seguridad del servidor
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //habilita cors y dehabilita la verificación de csrf
        http.cors().and().csrf().disable()
                //delega el manejo de solicitudes no autorizadas a nuestra clase authEntryPointJwt
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                //configuración para que todas las sesiónes sean stateless
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //autorizar a cualquiera para acceder a los recursos publicos
                .authorizeRequests().antMatchers(publicResources).permitAll()
                //pedir autenticación en cualquier otro recurso
                .anyRequest().authenticated();
        //añadir filtro para la validación de tokens
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    //Bean del passwordEncoder utilizado en las demas clases.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //configuración para usar nuestra implementacion de userDetailsService con nuestro passwordEncoder
    //en vez de la usada por defecto
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    //configuración para permitir siempre solicitudes tipo OPTIONS
    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
    }
    //bean del autenticationManager para usar en otras clases
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
