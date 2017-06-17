package com.jwtauth.service;

import com.jwtauth.model.User;
import com.jwtauth.repository.UserRepository;
import com.jwtauth.securityConfig.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marius on 6/12/2017.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(userName);

        if (user != null) {
            return new JwtUser(user);
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public JwtUser findUser(String userName) {

        User user = userRepository.findByUsername(userName);
        if (user != null) {
            return new JwtUser(user);
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<JwtUser> findAll() {

        List<User> users = userRepository.findAll();
        List<JwtUser> jwtUsers = new ArrayList<>();

        for (User user : users) {
            jwtUsers.add(new JwtUser(user));
        }
        return jwtUsers;
    }

}
