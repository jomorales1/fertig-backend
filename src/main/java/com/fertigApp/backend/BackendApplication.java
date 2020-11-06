package com.fertigApp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableWebSecurity
public class BackendApplication{ // extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
