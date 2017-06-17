package com.jwtauth.securityConfig;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marius on 6/12/2017.
 */

/**
 * Utility class for our token
 */
@Component
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_AUDIENCE = "audience";
    private static final String CLAIM_ROLE = "role";

    private Integer expirationInMinutes = 2;
    private transient Key secretKey = MacProvider.generateKey(); //creates a 512-bit secure-random key (the default):
    /*@Value("${config.security.secret}")
    private String secretKey;*/

    @Value("${config.security.issuer}")
    private String issuer;

    public String generateJwtToken(UserDetails userDetails, Device device) {

        Map<String, Object> claims = new HashMap<>();

        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_AUDIENCE, device);
        claims.put(CLAIM_ROLE, userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMinutes * 60000))
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, secretKey) //if we sign with 512 alg. we need also a 512-bit secretKey
                .compact();
    }

    public Claims getClaimsFromToken(String authToken) {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(authToken)
                    .getBody();
            return claims;
        } catch (Exception e) {
            //TODO Something
            return null;
        }
    }

    /*
     * Verify signature of token if is not altered, expiration date and issuer
     */
    public boolean tokenIsValid(String authToken) {

        Claims claims = getClaimsFromToken(authToken);

        if (claims == null || !new Date().before(claims.getExpiration()) || !claims.getIssuer().equals(issuer)) {
            return false;
        }
        return true;
    }
}
