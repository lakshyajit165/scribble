package com.scribble.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.scribble.authservice.dto.GenericAuthResponse;
import com.scribble.authservice.model.HttpStatusCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class helps to send customized forbidden message when a resource can't be accessed acc. to
 * SecurityConfiguration
 * */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        GenericAuthResponse genericAuthResponse = new GenericAuthResponse("Can't access resource without proper authentication");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(mapper.writeValueAsString(genericAuthResponse));
    }


}