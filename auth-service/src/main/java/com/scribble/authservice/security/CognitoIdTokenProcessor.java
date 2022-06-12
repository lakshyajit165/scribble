package com.scribble.authservice.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.scribble.authservice.config.JwtConfiguration;
import com.scribble.authservice.middlewares.CognitoJwtTokenFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.scribble.authservice.constants.CookieConstants.ID_TOKEN;
import static java.util.List.of;

@Component
public class CognitoIdTokenProcessor {

    private static final Log logger = LogFactory.getLog(CognitoIdTokenProcessor.class);

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> configurableJWTProcessor;

    public Authentication authenticate(HttpServletRequest request) throws Exception {
//        String idToken = request.getHeader(this.jwtConfiguration.getHttpHeader());

        Cookie[] cookies = request.getCookies();
//        if(cookies == cookies.length == 0)
//            return null;
//        String idToken = Arrays.stream(request.getCookies())
//                .filter(c -> ID_TOKEN.equals(c.getName()))
//                .map(Cookie::getValue)
//                .findAny().orElse(null);

//        logger.info("ID_TOKEN: " + idToken);
        if (cookies != null) {
            String idToken = Arrays.stream(request.getCookies())
                .filter(c -> ID_TOKEN.equals(c.getName()))
                .map(Cookie::getValue)
                .findAny().orElse(null);
            if(idToken == null)
                return null;
            JWTClaimsSet claims = this.configurableJWTProcessor.process(this.getBearerToken(idToken),null);
            String email = getEmailFromClaims(claims);
            validateIssuer(claims);
            verifyIfIdToken(claims);
            String username = getUserNameFromClaims(claims);
            if (username != null) {
                List<GrantedAuthority> grantedAuthorities = of( new SimpleGrantedAuthority("ROLE_ADMIN"));
                // setting the email from token as the "username" in security context
                User user = new User(email, "", of());
                return new JwtAuthentication(user, claims, grantedAuthorities);
            }
        }
        return null;
    }

    private String getUserNameFromClaims(JWTClaimsSet claims) {
        return claims.getClaims().get(this.jwtConfiguration.getUserNameField()).toString();
    }

    private String getEmailFromClaims(JWTClaimsSet claims) {
        return claims.getClaims().get(this.jwtConfiguration.getEmailField()).toString();
    }

    private void verifyIfIdToken(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception("JWT Token is not an ID Token");
        }
    }

    private void validateIssuer(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception(String.format("Issuer %s does not match cognito idp %s", claims.getIssuer(), this.jwtConfiguration.getCognitoIdentityPoolUrl()));
        }
    }

    private String getBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
    }
}
