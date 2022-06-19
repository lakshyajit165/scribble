package com.scribble.noteservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.scribble.noteservice.dto.GenericNotesResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        GenericNotesResponse genericAuthResponse = new GenericNotesResponse("Can't access resource without proper authentication");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(mapper.writeValueAsString(genericAuthResponse));
    }


}