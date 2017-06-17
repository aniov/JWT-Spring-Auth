package com.jwtauth.controller;

import com.jwtauth.securityConfig.JwtTokenUtil;
import com.jwtauth.securityConfig.JwtUser;
import com.jwtauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by Marius on 6/12/2017.
 */
@RestController
public class UserController {

    @Value("${config.security.header}")
    private String tokenHeader;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * This is a non-secured end point
     *
     * @return present Date
     */
    @GetMapping(path = "/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentDate() {
        return new ResponseEntity<>(new Date(), HttpStatus.OK);
    }

    /**
     * This is a secured end point, we expect a valid token sent in header from front-end who will be validated by our TokenFilter in Security
     *
     * @param request we take the username(unique email) from token so we can retrieve his credentials from DB
     * @return user for who the received token was emitted
     */
    @GetMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {

        String receivedToken = request.getHeader(tokenHeader);
        String username = jwtTokenUtil.getClaimsFromToken(receivedToken).getSubject();
        JwtUser jwtUser = userService.findUser(username);

        if (jwtUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jwtUser, HttpStatus.OK);
    }

    /**
     * This is a secured end point, we expect a valid token sent in header from front-end who will be validated by our TokenFilter in Security
     *
     * @return a list of users
     */
    @GetMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {

        List<JwtUser> jwtUsers = userService.findAll();
        if (jwtUsers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jwtUsers, HttpStatus.OK);
    }

}
