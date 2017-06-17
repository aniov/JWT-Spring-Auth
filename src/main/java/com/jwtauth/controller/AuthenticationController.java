package com.jwtauth.controller;

import com.jwtauth.securityConfig.JwtAuthenticationRequest;
import com.jwtauth.securityConfig.JwtAuthenticationResponse;
import com.jwtauth.securityConfig.JwtTokenUtil;
import com.jwtauth.service.UserService;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by Marius on 6/13/2017.
 */

/**
 * This is the login Class Controller
 * Here we generate a token in exchange of valid username & password
 */
@RestController
@RequestMapping(path = "/login")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginAndCreateToken(@RequestBody @Valid JwtAuthenticationRequest authenticationRequest, Device device, BindingResult result) {

        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateJwtToken(userDetails, device);

        return new ResponseEntity<>(new JwtAuthenticationResponse(token), HttpStatus.OK);
    }
}
