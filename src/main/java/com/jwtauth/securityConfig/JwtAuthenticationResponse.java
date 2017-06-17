package com.jwtauth.securityConfig;

import java.io.Serializable;

/**
 * Created by Marius on 6/15/2017.
 */

/**
 * A class just to transmit a serializable token
 */
public class JwtAuthenticationResponse implements Serializable {

    private String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
