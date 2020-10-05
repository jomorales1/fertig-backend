package com.fertigApp.backend.auth.configuration;

//import com.fertigApp.backend.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizacionServerConfiguration extends AuthorizationServerConfigurerAdapter {

    //Declaración del autheticationManager. Se usa Autowired, la Bean esta declarada en WebSecurityConfig
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    //TokenStore, almacena los tokens guardados, estructura en la base de datos
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(this.tokenStore)
                .authenticationManager(this.authenticationManager);
                //.userDetailsService(userDetailsService);
    }

    //En esta función se declara en los GranTypes válidos, el rol y el scope de la autenticación.
    // La aplicación cliente debera considir con el cliente y secret aqúi declarados.
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("cliente")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .authorities("USER")
                .scopes("read", "write")
                .resourceIds("rest_service")
                .secret("secret");
                //.accessTokenValiditySeconds(24 * 365 * 60 * 60); //Modificación de la duración de la validez del Token.
                //.autoApprove(true)
    }

    //Bean del passwordEncoder utilizado en las demas clases.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Override de los metodos encode y matches de la autenticación.
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception{
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword != null ? rawPassword.toString() : null;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword != null && encodedPassword != null && rawPassword.toString().equals(encodedPassword);
            }
        };
        oauthServer.passwordEncoder(passwordEncoder);
    }

    //Declaración del tokenService.
    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(this.tokenStore);
        return tokenServices;
    }

}
