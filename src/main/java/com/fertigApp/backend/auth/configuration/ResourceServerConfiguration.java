package com.fertigApp.backend.auth;

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

    private static final String[] publicResources = new String[]{"/registro/nuevo-usuario"};
    private static final String[] userResources = new String[]{"/usuario/**"};

//    @Autowired
//    private DefaultTokenServices tokenService;

    @Autowired
    private TokenStore tokenStore;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/oauth/token","/oauth/authorize**").permitAll(); //publica
//        http.requestMatchers().antMatchers("/add");

//        http.requestMatchers().antMatchers("/privada")
//                .and().authorizeRequests()
//                .antMatchers("/privada").access("hasRole('USER')")
//                .and().requestMatchers().antMatchers("/admin")
//                .and().authorizeRequests()
//                .antMatchers("/admin").access("hasRole('ADMIN')");

        http.requestMatchers().antMatchers("/all")
                .and().authorizeRequests()
                .antMatchers("/all").access("hasRole('USER')");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenService());
        resources.resourceId(null);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(this.tokenStore);
        return tokenServices;
    }
}
