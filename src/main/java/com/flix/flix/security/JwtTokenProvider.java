package com.flix.flix.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${expiration.time}")
    private Long EXPIRATION_TIME;
    @Value("${secret.key}")
    private String SECRET_KEY;

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getSubject();
    }

    public Boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = getDecodedJWT(token);
            return !decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        String role = decodedJWT.getClaim("role").asString();

        if (role.startsWith("[") && role.endsWith("]")) {
            role = role.substring(1, role.length() - 1);
        }

        return role;
    }

    public String generateToken(String username, String role) {
        // System.out.println("INININ ROLE"+role);
        // if (role.startsWith("[[") && role.endsWith("]]")) {
        //     role = role.substring(2, role.length() - 2);
        //     System.out.println("INININ ROLE"+role);
        // }
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET_KEY));

    }

    private DecodedJWT getDecodedJWT(String token) {
        return JWT.require(Algorithm.HMAC512(SECRET_KEY))
                .build()
                .verify(token);
    }

    public Long getExpirationTime(String token) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        Date expirationDate = decodedJWT.getExpiresAt();
        return expirationDate.getTime() - System.currentTimeMillis();
    }
}
