package com.fertigApp.backend.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.RestController;

@EnableResourceServer
@RestController
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String[] publicResources = new String[]
            {
                    "/oauth/token",
                    "/oauth/authorize**",
                    "/users/addUser"
            };

    private static final String[] userResources = new String[]
            {
                    "/users/get",
                    "/users/update",
                    "/users/delete/",
                    "/tasks/getTasks",
                    "/tasks/getTask/**",
                    "/tasks/updateTask/**",
                    "/tasks/addTask",
                    "/tasks/deleteTask/**"
            };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(publicResources).permitAll(); //publica
//        http.requestMatchers().antMatchers("/add");

//        http.requestMatchers().antMatchers("/privada")
//                .and().authorizeRequests()
//                .antMatchers("/privada").access("hasRole('USER')")
//                .and().requestMatchers().antMatchers("/admin")
//                .and().authorizeRequests()
//                .antMatchers("/admin").access("hasRole('ADMIN')");

        http.requestMatchers().antMatchers(userResources)
                .and().authorizeRequests()
                .antMatchers(userResources).access("hasRole('USER')");
    }
}
