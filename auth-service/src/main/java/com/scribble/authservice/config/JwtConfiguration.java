package com.scribble.authservice.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.scribble.authservice.constants.CognitoConstants.*;

@Component
@Configuration
@NoArgsConstructor
public class JwtConfiguration {

    @Value(value = "${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value(value = "${aws.cognito.jwk}")
    private String jwkUrl;

    @Value(value = "${aws.cognito.region}")
    private String region;

    public String getJwkUrl() {
        return this.jwkUrl != null && !this.jwkUrl.isEmpty() ? this.jwkUrl : String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", this.region, this.userPoolId);
    }

    public String getCognitoIdentityPoolUrl() {
        return String.format("https://cognito-idp.%s.amazonaws.com/%s", this.region, this.userPoolId);
    }

    public String getUserNameField() {
        return USER_NAME_FIELD;
    }

    public String getEmailField() { return EMAIL_FIELD; }

    public int getConnectionTimeout() {
        return CONNECTION_TIMEOUT;
    }

    public int getReadTimeout() {
        return READ_TIMEOUT;
    }

    public String getHttpHeader() {
        return HTTP_HEADER;
    }
}
