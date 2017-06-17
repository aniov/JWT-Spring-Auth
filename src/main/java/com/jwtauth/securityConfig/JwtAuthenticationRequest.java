package com.jwtauth.securityConfig;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Marius on 6/12/2017.
 */
public class JwtAuthenticationRequest {

    @Email
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public JwtAuthenticationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
