package com.fertigApp.backend.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ClientConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableResourceServer
//@RestController
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    //Declaración del tokenService declarado en AuthorizationServiceCongig
    @Autowired
    private DefaultTokenServices tokenService;

    //URL's de los recursos públicos (no requieren Token)
    private static final String[] publicResources = new String[]
            {
                    "/oauth/token",
                    "/oauth/authorize**",
                    "/users/addUser"
            };

    //URL's privadas, requieren autenticación con Token.
    private static final String[] userResources = new String[]
            {
                    "/users/get",
                    "/users/getAllUsers",
                    "/users/update",
                    "/users/delete/",
                    "/tasks/getTasks",
                    "/tasks/getTask/**",
                    "/tasks/updateTask/**",
                    "/tasks/addTask",
                    "/tasks/deleteTask/**"
            };


    //Configuración de los roles que pueden acceder a los recursos privados y publicos.
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(publicResources).permitAll(); //publica

        http.requestMatchers().antMatchers(userResources)
                .and().authorizeRequests()
                .antMatchers(userResources).access("hasRole('USER')");

        //      .and().authorizeRequests() // Eventual implementación de otros  permisos
        //      .antMatchers("/admin").access("hasRole('ADMIN')"); //Para otros roles
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenService);
        resources.resourceId(null); //Corrige el error en el resourceId.
    }


}
