package com.jwtauth.securityConfig;

import com.jwtauth.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Marius on 6/12/2017.
 */

/**
 * This class will be invoked by SpringSecurity at every request made by front-end
 * If the token provided in header is valid, the user will pass the security check
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${config.security.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authToken = httpServletRequest.getHeader(tokenHeader);

        if (authToken != null && jwtTokenUtil.tokenIsValid(authToken)) {

            Claims claims = jwtTokenUtil.getClaimsFromToken(authToken);

            String username = claims.getSubject();

            //Check if user still exists, if is not logged in, we log him in
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
