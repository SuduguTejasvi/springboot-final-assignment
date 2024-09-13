package com.tejasvi.api_gateway_final.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JWTService {
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private final SecretKey key;
    private final JwtParser parser;

    public JWTService(){
        this.key = Keys.hmacShaKeyFor("jnbhgvfcd2345678hgvfcdbgvfc34567".getBytes());
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    public String generate(String userName) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(key);
        return builder.compact();
    }

    public String getUserName(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validate(UserDetails userDetails, String token) {
        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            boolean unexpired = claims.getExpiration().after(Date.from(Instant.now()));
            boolean validUser = userDetails.getUsername().equals(claims.getSubject());
            logger.debug("Token validation - Expiration: {}, Valid User: {}", unexpired, validUser);
            return unexpired && validUser;
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}
