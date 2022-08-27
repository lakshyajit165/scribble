package com.scribble.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS is configured globally on API Gateway, so configuring it again in any downstream microservices
 * gives an error like:
 * "Access to XMLHttpRequest at 'http://localhost:9000/auth-service/api/v1/auth/sign_up'
 * from origin 'http://localhost:4200' has been blocked by CORS policy:
 * The 'Access-Control-Allow-Origin' header contains multiple values 'http://localhost:4200,
 * http://localhost:4200', but only one is allowed."
 *
 * Uncomment this config class when this microservice is used as a standalone service without the
 * gateway
 * */
//@Configuration
//public class CorsConfiguration {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowCredentials(true)
//                        .allowedMethods("*")
//                        .allowedOrigins("http://localhost:4200")
//                        .allowedHeaders("*");
//            }
//        };
//    }
//}
