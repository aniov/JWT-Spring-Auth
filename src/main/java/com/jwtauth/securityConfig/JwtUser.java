package com.jwtauth.securityConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwtauth.model.Authority;
import com.jwtauth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marius on 6/13/2017.
 */
public class JwtUser implements UserDetails {

    @JsonIgnore
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    //@JsonIgnore
    private String firstname;

    //@JsonIgnore
    private String lastname;

    private List<GrantedAuthority> authorities;

    public JwtUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.authorities = authoritiesToGrantedAuthorities(user.getAuthorities());
    }

    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private List<GrantedAuthority> authoritiesToGrantedAuthorities(List<Authority> authorities) {

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityType().name()));
        }
        return grantedAuthorities;
    }
}
