package com.jwtauth.security;

import com.jwtauth.securityConfig.JwtAuthenticationEntryPoint;
import com.jwtauth.securityConfig.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by Marius on 6/12/2017.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

    /** We encode input password from Login page with BCrypt and then compare it with the one in DB*/
    @Autowired
    public void encryptPassword(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring()
                .antMatchers("/js/*",
                        "/css/*"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable() //No need for CSRF
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)//We handle possible errors thrown by DB query
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //No need for Session
                .and()
                .authorizeRequests()
                .antMatchers("/",
                        "/login",
                        "/date")
                .permitAll()
                .anyRequest().authenticated()
                .and()/**We use our custom filter to check the token first */
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .headers().cacheControl();
    }
}
