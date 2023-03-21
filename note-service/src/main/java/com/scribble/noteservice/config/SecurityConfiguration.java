package com.scribble.noteservice.config;

import com.scribble.noteservice.middlewares.CognitoJwtTokenFilter;
import com.scribble.noteservice.security.JwtAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.scribble.noteservice.constants.URLConstants.TEST_URL;
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

        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .authorizeHttpRequests((authz) -> {
                            try {
                                authz.requestMatchers(
                                                "/",
                                                "/error",
                                                "/favicon.ico",
                                                "/*/*.png",
                                                "/*/*.gif",
                                                "/*/*.svg",
                                                "/*/*.jpg",
                                                "/*/*.html",
                                                "/*/*.css",
                                                "/*/*.js",
                                                TEST_URL
                                        ).permitAll()
                                        .anyRequest()
                                        .authenticated();


                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }
                )
                .httpBasic(withDefaults());

        // Add our custom Token based authentication filter
        http.addFilterBefore(cognitoJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}