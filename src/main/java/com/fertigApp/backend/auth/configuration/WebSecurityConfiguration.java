package com.fertigApp.backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
//    @Autowired
//    public DataSource dataSource;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception
//    {
//        auth.userDetailsService(jdbcUserDetailsManager()).passwordEncoder(passwordEncoder());
//    }


    private UserDetailsService userDetailsService;

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.builder().username("user").password(passwordEncoder().encode("secret")).
//                roles("USER").build();
//        return new InMemoryUserDetailsManager(user); //userAdmin
//    }

    public WebSecurityConfiguration( UserDetailsService userDetailsService ){
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure( AuthenticationManagerBuilder builder ) throws Exception{
        builder.userDetailsService( userDetailsService( ) ).passwordEncoder( passwordEncoder( ) );
    }

    @Override
    protected UserDetailsService userDetailsService( ){
        return userDetailsService;
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

        http.csrf().disable();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Bean
//    public JdbcUserDetailsManager jdbcUserDetailsManager()
//    {
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
//        jdbcUserDetailsManager.setDataSource(dataSource);
//
//
//
//        jdbcUserDetailsManager.setUserExistsSql("select username from mydb.users where username = ?");
//        jdbcUserDetailsManager.setCreateUserSql("insert into mydb.users (username, password, enabled) values (?,?,?)");
//        jdbcUserDetailsManager.setCreateAuthoritySql("insert into mydb.authorities (username, authority) values (?,?)");
//        jdbcUserDetailsManager.setUpdateUserSql("update mydb.users set password = ?, enabled = ? where username = ?");
//        jdbcUserDetailsManager.setDeleteUserSql("delete from mydb.users where username = ?");
//        jdbcUserDetailsManager.setDeleteUserAuthoritiesSql("delete from mydb.authorities where username = ?");
//
//        return jdbcUserDetailsManager;
//    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//
//        UserDetails user = User.builder().username("user").password(passwordEncoder().encode("secret")).
//                roles("USER").build();
//        //UserDetails userAdmin=User.builder().username("admin").password(passwordEncoder().encode("secret")).
//        //roles("ADMIN").build();
//        return new InMemoryUserDetailsManager(user); //userAdmin
//    }

}
