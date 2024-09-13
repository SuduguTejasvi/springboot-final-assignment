package com.tejasvi.api_gateway_final.controller;

import com.tejasvi.api_gateway_final.entity.ReqRespModel;
import com.tejasvi.api_gateway_final.entity.User;
import com.tejasvi.api_gateway_final.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    final ReactiveUserDetailsService users;

    final JWTService jwtService;

    final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(ReactiveUserDetailsService users, JWTService jwtService, PasswordEncoder passwordEncoder){
        this.users=users;
        this.jwtService=jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth")
    public Mono<ResponseEntity<ReqRespModel<String>>> auth(){
        return Mono.just(ResponseEntity.ok(
                new ReqRespModel<>("Welcome to the fitness center","")
        ));
    }
    @PostMapping("/login")
    public Mono<ResponseEntity<ReqRespModel<String>>> login(@RequestBody User user){
       Mono<UserDetails> foundUser=users.findByUsername(user.getUsername()).defaultIfEmpty(null);
       return foundUser.flatMap(
               u->{
                   if(u!=null){
                       if(passwordEncoder.matches(user.getPassword(),u.getPassword()))
                       {
                           return Mono.just(
                                   ResponseEntity.ok(
                                           new ReqRespModel<>(jwtService.generate(u.getUsername()),"Success")
                                   )
                           );
                       }
                       return Mono.just(
                               ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>("","Invalid credentials"))
                       );
                   }
                   return Mono.just(
                           ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqRespModel<>("","User not found"))
                   );
               }
       );
    }
}
