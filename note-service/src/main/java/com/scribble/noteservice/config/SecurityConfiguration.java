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

        http.csrf().disable()
                .authorizeHttpRequests((authz) -> {
                            try {
                                authz
                                        .antMatchers(TEST_URL).permitAll()
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


