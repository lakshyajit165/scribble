package com.scribble.authservice.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.scribble.authservice.dto.GenericAuthResponse;
import com.scribble.authservice.model.AuthErrorCode;
import com.scribble.authservice.security.CognitoIdTokenProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CognitoJwtTokenFilter extends OncePerRequestFilter {

    private static final Log logger = LogFactory.getLog(CognitoJwtTokenFilter.class);

    @Autowired
    private CognitoIdTokenProcessor cognitoIdTokenProcessor;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication;
        try {
            authentication = this.cognitoIdTokenProcessor.authenticate(request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error(e.getMessage());
            SecurityContextHolder.clearContext();
            ObjectMapper mapper = new ObjectMapper();
            GenericAuthResponse genericAuthResponse = new GenericAuthResponse(AuthErrorCode.BAD_REQUEST, e.getMessage());
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(mapper.writeValueAsString(genericAuthResponse));
        }

    }
}
