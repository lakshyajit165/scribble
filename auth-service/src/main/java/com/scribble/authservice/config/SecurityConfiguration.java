package com.scribble.authservice.config;


import com.scribble.authservice.controller.AuthController;
import com.scribble.authservice.middlewares.CognitoJwtTokenFilter;
import com.scribble.authservice.security.JwtAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.scribble.authservice.constants.URLConstants.*;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfiguration {

    @Autowired
    private CognitoJwtTokenFilter cognitoJwtTokenFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /**
         * Adds a CorsFilter to be used. If a bean by the name of corsFilter is provided,
         * that CorsFilter is used.
         * Else if corsConfigurationSource is defined, then that CorsConfiguration is used.
         * Otherwise, if Spring MVC is on the classpath a HandlerMappingIntrospector is used.
         * */
        http.cors()
                .and().csrf().disable()
                .authorizeHttpRequests((authz) -> {
                            try {
                                authz.requestMatchers(
                                                "**/health",
                                                SIGNUP_URL,
                                                SIGNIN_URL,
                                                FORGOT_PASSWORD_URL,
                                                CONFIRM_PASSWORD_URL,
                                                CONFIRM_FORGOT_PASSWORD_URL,
                                                GET_NEW_CREDS_URL,
                                                TEST_URL).permitAll()
                                        .anyRequest().authenticated()
                                        .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                        .and().addFilterBefore(cognitoJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
                            } catch (Exception e) {
                               logger.error(e.getMessage());
                            }
                        }
                )
                .httpBasic(withDefaults());
        return http.build();
    }

}


