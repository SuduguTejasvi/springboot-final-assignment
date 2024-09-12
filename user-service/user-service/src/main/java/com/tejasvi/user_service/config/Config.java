package com.tejasvi.user_service.config;

import com.tejasvi.user_service.util.Helpers;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class Config {

    @Bean
    public ModelMapper modelMapper(){
        log.info("instanced modelmapper");
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Helpers helpers(){
        return new Helpers();
    }
}
