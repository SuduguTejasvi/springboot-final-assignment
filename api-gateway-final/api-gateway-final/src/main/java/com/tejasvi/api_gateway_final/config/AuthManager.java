package com.tejasvi.api_gateway_final.config;

import com.tejasvi.api_gateway_final.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthManager implements ReactiveAuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

    private final JWTService jwtService;
    private final ReactiveUserDetailsService users;

    @Autowired
    public AuthManager(JWTService jwtService, ReactiveUserDetailsService users) {
        this.jwtService = jwtService;
        this.users = users;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    String username = jwtService.getUserName(auth.getCredentials());
                    logger.debug("Extracted username from token: {}", username);

                    return users.findByUsername(username)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found in auth manager")))
                            .flatMap(userDetails -> {
                                boolean isValidToken = jwtService.validate(userDetails, auth.getCredentials());
                                logger.debug("Token validity: {}", isValidToken);

                                if (isValidToken) {
                                    return Mono.just(
                                            new UsernamePasswordAuthenticationToken(
                                                    userDetails.getUsername(),
                                                    userDetails.getPassword(),
                                                    userDetails.getAuthorities()
                                            )
                                    );
                                } else {
                                    return Mono.error(new IllegalArgumentException("Expired or invalid token"));
                                }
                            });
                });
    }
}
