package com.scribble.authservice.controller;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.scribble.authservice.dto.*;
import com.scribble.authservice.model.HttpStatusCode;
import com.scribble.authservice.model.CognitoUserAccountStatus;
import com.scribble.authservice.model.CognitoUserStatus;
import com.scribble.authservice.utils.PasswordGenerator;
import org.hibernate.validator.internal.util.DomainNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.scribble.authservice.constants.CookieConstants.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AWSCognitoIdentityProvider cognitoClient;

    @Value(value = "${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value(value = "${aws.cognito.clientId}")
    private String clientId;

    @Value(value = "${aws.access-secret}")
    private String secretKey;

    @Value(value = "${cookieDomain}")
    private String cookieDomain;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUpRequest(@Valid @RequestBody UserSignUpOrForgotPasswordRequest userSignUpOrForgotPasswordRequest){
        AttributeType emailAttribute =
                new AttributeType().withName("email").withValue(userSignUpOrForgotPasswordRequest.getEmail());
        AttributeType emailVerifiedAttribute =
                new AttributeType().withName("email_verified").withValue("true");
        try {
            // admingetuser -> if user exists and state is 'FORCE_CHANGE_PASSWORD' delete the user entry(and verification code will be sent again)
            CognitoUserDetailsCustomResponse cognitoUserDetailsCustomResponse = getCognitoUserDetails(userSignUpOrForgotPasswordRequest.getEmail());
            if(cognitoUserDetailsCustomResponse.getCognitoUserStatus().equals(CognitoUserStatus.FOUND)) {
                if(cognitoUserDetailsCustomResponse.getCognitoUserAccountStatus().equals(CognitoUserAccountStatus.FORCE_CHANGE_PASSWORD)){
                    deleteCognitoUser(userSignUpOrForgotPasswordRequest.getEmail());
                    logger.info("Previous user with same email, deleted");
                }
                if(cognitoUserDetailsCustomResponse.getCognitoUserAccountStatus().equals(CognitoUserAccountStatus.CONFIRMED))
                    return ResponseEntity.status(400).body(new GenericAuthResponse("A user with this email already exists!"));
            }

            AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest()
                    .withUserPoolId(userPoolId).withUsername(userSignUpOrForgotPasswordRequest.getEmail())
                    .withTemporaryPassword(new PasswordGenerator().generateSecurePassword())
                    .withUserAttributes(emailAttribute, emailVerifiedAttribute)
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);

            cognitoClient.adminCreateUser(createUserRequest);
            logger.info("User created");
            return ResponseEntity.status(200).body(new GenericAuthResponse("Verification code sent!"));

        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/confirm_password")
    public ResponseEntity<?> confirmPasswordRequest(@Valid @RequestBody UserConfirmPasswordRequest userConfirmPasswordRequest) {

        final Map<String, String> confirmPasswordRequestParams = new HashMap<>();
        confirmPasswordRequestParams.put("USERNAME", userConfirmPasswordRequest.getEmail());
        confirmPasswordRequestParams.put("PASSWORD", userConfirmPasswordRequest.getVerification_code());

        final Map<String, String> newPasswordRequestParams = new HashMap<>();
        newPasswordRequestParams.put("USERNAME", userConfirmPasswordRequest.getEmail());
        newPasswordRequestParams.put("NEW_PASSWORD", userConfirmPasswordRequest.getPassword());

        final AdminInitiateAuthRequest confirmPasswordRequest = new AdminInitiateAuthRequest();
        confirmPasswordRequest
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(clientId)
                .withUserPoolId(userPoolId)
                .withAuthParameters(confirmPasswordRequestParams);

        try {
            AdminInitiateAuthResult confirmPasswordResult = cognitoClient.adminInitiateAuth(confirmPasswordRequest);
            if(confirmPasswordResult.getChallengeName().equals("NEW_PASSWORD_REQUIRED")){
                final AdminRespondToAuthChallengeRequest adminRespondToConfirmPasswordRequest = new AdminRespondToAuthChallengeRequest();
                adminRespondToConfirmPasswordRequest
                        .withChallengeName("NEW_PASSWORD_REQUIRED")
                        .withClientId(clientId)
                        .withUserPoolId(userPoolId)
                        .withChallengeResponses(newPasswordRequestParams)
                        .withSession(confirmPasswordResult.getSession());
                cognitoClient.adminRespondToAuthChallenge(adminRespondToConfirmPasswordRequest);
                return ResponseEntity.status(200).body(new GenericAuthResponse("SignUp complete"));
            }
            return ResponseEntity.status(500).body(new GenericAuthResponse("Error signing in user"));
        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/sign_in")
    public ResponseEntity<?> signInRequest(@Valid @RequestBody UserSignInRequest userSignInRequest){

        final Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", userSignInRequest.getEmail());
        authParams.put("PASSWORD", userSignInRequest.getPassword());

        final AdminInitiateAuthRequest signInRequest = new AdminInitiateAuthRequest();
        signInRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).withClientId(clientId)
                .withUserPoolId(userPoolId).withAuthParameters(authParams);

        try {
            AdminInitiateAuthResult signInInitiateResult = cognitoClient.adminInitiateAuth(signInRequest);
            AuthenticationResultType signInCompleteResult = signInInitiateResult.getAuthenticationResult();

            ResponseCookie idTokenCookie = ResponseCookie.from(ID_TOKEN, signInCompleteResult.getIdToken())
                    .httpOnly(true).domain(cookieDomain).path("/")
                    .build();
            ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN, signInCompleteResult.getRefreshToken())
                    .httpOnly(true).domain(cookieDomain).path("/")
                    .build();
            return ResponseEntity
                    .status(200)
                    .header(HttpHeaders.SET_COOKIE, idTokenCookie.toString(), refreshTokenCookie.toString())
                    .body(new GenericAuthResponse("User signed in."));

        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPasswordRequest(@Valid @RequestBody UserSignUpOrForgotPasswordRequest userSignUpOrForgotPasswordRequest){
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest
                .withClientId(clientId)
                .withUsername(userSignUpOrForgotPasswordRequest.getEmail());
        try {
            ForgotPasswordResult forgotPasswordResult = cognitoClient.forgotPassword(forgotPasswordRequest);
            logger.info(forgotPasswordResult.toString());
            return ResponseEntity.status(200).body(new GenericAuthResponse("Verification code sent to your email"));

        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/confirm_forgot_password")
    public ResponseEntity<?> confirmForgotPassword(@Valid @RequestBody UserConfirmForgotPasswordRequest userConfirmForgotPasswordRequest){

        ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
        confirmForgotPasswordRequest
                .withClientId(clientId)
                .withConfirmationCode(userConfirmForgotPasswordRequest.getVerification_code())
                .withUsername(userConfirmForgotPasswordRequest.getEmail())
                .withPassword(userConfirmForgotPasswordRequest.getPassword());

        try {

            ConfirmForgotPasswordResult confirmForgotPasswordResult = cognitoClient.confirmForgotPassword(confirmForgotPasswordRequest);
            logger.info(confirmForgotPasswordResult.toString());
            return ResponseEntity.status(200).body(new GenericAuthResponse("Password reset successful"));

        }  catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse("Error confirming new password"));
        }
    }

    @GetMapping("/get_new_creds")
    public ResponseEntity<?> getNewTokensUsingRefreshToken(@CookieValue("refresh_token") String refreshToken) {
        /**
         * This route - while NOT included in the spring security config, works
         * because in the "authenticated" section we have only defined specific set of routes.
         * Added this route under the permitAll section but NOT adding it also doesn't make
         * a difference in terms of accessibility.
         * Note: It wasn't added initially in the authenticated section because security context
         * doesn't hold refresh-token acc. to the current functionality.
         * */

        if(refreshToken == null || refreshToken.isEmpty())
            return ResponseEntity.status(400).body(new GenericAuthResponse("Refresh token missing from header"));

        final Map<String, String> authParams = new HashMap<>();
        authParams.put("REFRESH_TOKEN", refreshToken);

        final AdminInitiateAuthRequest getNewTokensRequest = new AdminInitiateAuthRequest();
        getNewTokensRequest.withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH).withClientId(clientId)
                .withUserPoolId(userPoolId).withAuthParameters(authParams);
        try {
            AdminInitiateAuthResult adminInitiateAuthResult = cognitoClient.adminInitiateAuth(getNewTokensRequest);
            AuthenticationResultType getNewTokensResult = adminInitiateAuthResult.getAuthenticationResult();
            /**
             * Send new creds in the response as a http-only cookie
             * Reference: https://reflectoring.io/spring-boot-cookies/
             * (Could add "secure" and "domain" option in production)
             * */
            ResponseCookie idTokenCookie = ResponseCookie.from("id_token", getNewTokensResult.getIdToken())
                    .httpOnly(true).domain(cookieDomain).path("/")
                    .build();
            return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, idTokenCookie.toString()).body(new GenericAuthResponse("Fetched new creds successfully!"));
        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse("Error confirming new password"));
        }
    }

    /**
     * returns
     * {
     *     "message": "Expired JWT" - incase of expired token(sent by CognitoJwtTokenFilter)
     * }
     * or
     * {
     *     "message": true(or)false - true in case the token is valid, false, if the token
     *     can't be parsed somehow
     * }
     *
     * */
    @GetMapping("/is_loggedin")
    public ResponseEntity<?> isUserLoggedIn(@CookieValue("id_token") String idToken){
        try {
            if(idToken != null) {
                SignedJWT signedJWT = SignedJWT.parse(idToken);
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                Map<String, Object> idTokenClaim = claimsSet.getClaims();
                // if expiry_at timestamp is greater than current timestamp, return false, else return true
                if(idTokenClaim.get("exp").toString().compareTo(idTokenClaim.get("iat").toString()) > 0)
                    return ResponseEntity.status(200).body(new GenericAuthResponse("true"));
                return ResponseEntity.status(200).body(new GenericAuthResponse("false"));
            }else{
                return ResponseEntity.status(400).body(new GenericAuthResponse("false"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new GenericAuthResponse("false"));
        }


    }

    @PostMapping("/logout")
    public ResponseEntity<?> userLogout(Authentication authentication) {
        String email = authentication.getName();
        if(email == null || email.isEmpty()){
            return ResponseEntity.status(500).body(new GenericAuthResponse("Error parsing email from headers"));
        }
        try {
            // logout logic here
            /**
             * Signs out users from all devices, as an administrator.
             * It also invalidates all refresh tokens issued to a user. The user's current access
             * and Id tokens remain valid until their expiry. Access and Id tokens expire
             * one hour after they're issued.
             * */
            AdminUserGlobalSignOutRequest adminUserGlobalSignOutRequest = new AdminUserGlobalSignOutRequest();
            adminUserGlobalSignOutRequest
                    .withUsername(email)
                    .withUserPoolId(userPoolId);
            AdminUserGlobalSignOutResult adminUserGlobalSignOutResult = cognitoClient.adminUserGlobalSignOut(adminUserGlobalSignOutRequest);
            return ResponseEntity.status(200).body(new GenericAuthResponse("User logged out"));
        } catch (AWSCognitoIdentityProviderException e) {
            /**
             * Base exception for all service exceptions thrown by Amazon Cognito Identity Provider
             * */
            logger.error(e.getErrorMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse(e.getErrorMessage()));
        }  catch(Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new GenericAuthResponse("Error logging out user"));
        }
    }

    @GetMapping("/resource")
    public ResponseEntity<?> getProtectedResource(Authentication authentication) {
//        logger.info(authentication.);
            logger.info(authentication.getName());
            return ResponseEntity.status(200).body(new GenericAuthResponse("Access granted! Hello " + authentication.getName()));
    }

    @GetMapping("/resource2")
    public ResponseEntity<?> getProtectedResource2() {
//        logger.info(authentication.);
        return ResponseEntity.status(200).body(new GenericAuthResponse("Access granted 2!"));
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTestResponse() {
        return ResponseEntity.status(200).body(new GenericAuthResponse("Auth service is up!"));
    }

    private CognitoUserDetailsCustomResponse getCognitoUserDetails(String email) {
        try {
            AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
                    .withUserPoolId(userPoolId).withUsername(email);
            AdminGetUserResult getUserResult = cognitoClient.adminGetUser(getUserRequest);
            return new CognitoUserDetailsCustomResponse(CognitoUserStatus.FOUND, CognitoUserAccountStatus.valueOf(getUserResult.getUserStatus()), "User found");
        } catch (UserNotFoundException e) {
            return new CognitoUserDetailsCustomResponse(CognitoUserStatus.NOT_FOUND, CognitoUserAccountStatus.UNKNOWN, "No user found");
        }

    }

    private void deleteCognitoUser(String email) {
        AdminDeleteUserRequest deleteUserRequest = new AdminDeleteUserRequest()
                .withUserPoolId(userPoolId).withUsername(email);
        cognitoClient.adminDeleteUser(deleteUserRequest);
    }

}
