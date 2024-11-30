package com.scribble.authservice.controller;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.scribble.authservice.dto.*;
import com.scribble.authservice.model.CognitoUserAccountStatus;
import com.scribble.authservice.model.CognitoUserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AWSCognitoIdentityProvider cognitoClient;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Use ReflectionTestUtils to set private fields
        ReflectionTestUtils.setField(authController, "userPoolId", "mockUserPoolId");
        ReflectionTestUtils.setField(authController, "clientId", "mockClientId");
        ReflectionTestUtils.setField(authController, "secretKey", "mockSecretKey");
        ReflectionTestUtils.setField(authController, "cookieDomain", "mockCookieDomain");
    }

    @Test
    void testAuthControllerProperties() {
        // Verify the values
        assertEquals("mockUserPoolId", ReflectionTestUtils.getField(authController, "userPoolId"));
        assertEquals("mockClientId", ReflectionTestUtils.getField(authController, "clientId"));
        assertEquals("mockSecretKey", ReflectionTestUtils.getField(authController, "secretKey"));
        assertEquals("mockCookieDomain", ReflectionTestUtils.getField(authController, "cookieDomain"));
    }

    @Test
    void testSignUpRequest_UserAlreadyExists() {
        // Mock response from Cognito
        AdminGetUserResult mockGetUserResult = new AdminGetUserResult()
                .withUsername("existing_user@test.com")
                /**
                 * In the controller we are using the CognitoUserDetailsCustomResponse
                 * DTO but here it's not required as we can mock the user account status
                 * directly instead of mapping
                 * */
                .withUserStatus(String.valueOf(CognitoUserAccountStatus.CONFIRMED));
        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class)))
                .thenReturn(mockGetUserResult);

        UserSignUpOrForgotPasswordRequest request = new UserSignUpOrForgotPasswordRequest();
        request.setEmail("existing_user@test.com");

        ResponseEntity<?> response = authController.signUpRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("A user with this email already exists!", ((GenericAuthResponse) response.getBody()).getMessage());

        verify(cognitoClient, times(1)).adminGetUser(any(AdminGetUserRequest.class));
    }

    @Test
    void testSignUpRequest_NewUserCreation() {
        /**
         * The below piece of code to configure cognitoClient is just a dummy
         * implementation to pass some values to cognitoClient and only to
         * avoid NPE / similar errors
         * */
        AdminGetUserResult mockGetUserResult = new AdminGetUserResult()
                .withUsername("test_user@test.com")

                .withUserStatus(String.valueOf(CognitoUserAccountStatus.UNCONFIRMED));
        when(cognitoClient.adminGetUser(any(AdminGetUserRequest.class)))
                .thenReturn(mockGetUserResult);

        // Testing signup logic

        UserSignUpOrForgotPasswordRequest request = new UserSignUpOrForgotPasswordRequest();
        request.setEmail("existing_user@test.com");

        ResponseEntity<?> response = authController.signUpRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Verification code sent!", ((GenericAuthResponse) response.getBody()).getMessage());

    }


    @Test
    void testSignInRequest_SuccessfulSignIn() {
        // Mock Cognito response
        AdminInitiateAuthResult mockAuthResult = new AdminInitiateAuthResult()
                .withAuthenticationResult(new AuthenticationResultType()
                        .withIdToken("mockIdToken")
                        .withRefreshToken("mockRefreshToken"));
        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class)))
                .thenReturn(mockAuthResult);

        UserSignInRequest request = new UserSignInRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");

        ResponseEntity<?> response = authController.signInRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Set-Cookie"));
        assertEquals("User signed in.", ((GenericAuthResponse) response.getBody()).getMessage());

        verify(cognitoClient, times(1)).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
    }

    @Test
    void testSignInRequest_InvalidCredentials() {
        // Mock Cognito exception
        AWSCognitoIdentityProviderException mockException = new AWSCognitoIdentityProviderException("Invalid credentials");
        mockException.setErrorCode("NotAuthorizedException");
        mockException.setStatusCode(401);

        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class)))
                .thenThrow(mockException);

        // Prepare the request
        UserSignInRequest request = new UserSignInRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrongPassword");

        // Call the controller method
        ResponseEntity<?> response = authController.signInRequest(request);

        // Assertions
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode(), "Expected HTTP status code 401");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertInstanceOf(GenericAuthResponse.class, response.getBody(), "Response body should be of type GenericAuthResponse");

        GenericAuthResponse authResponse = (GenericAuthResponse) response.getBody();
        assertEquals("Invalid credentials", authResponse.getMessage(), "Expected error message: 'Invalid credentials'");

        // Verify the mocked behavior
        verify(cognitoClient, times(1)).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
    }

    @Test
    void testForgotPasswordRequest() {
        // Mock Cognito response
        when(cognitoClient.forgotPassword(any(ForgotPasswordRequest.class)))
                .thenReturn(new ForgotPasswordResult());

        UserSignUpOrForgotPasswordRequest request = new UserSignUpOrForgotPasswordRequest();
        request.setEmail("user@test.com");

        ResponseEntity<?> response = authController.forgotPasswordRequest(request);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Verification code sent to your email", ((GenericAuthResponse) response.getBody()).getMessage());

        verify(cognitoClient, times(1)).forgotPassword(any(ForgotPasswordRequest.class));
    }

    @Test
    void testConfirmForgotPasswordRequest_Success() {
        // Mock Cognito response
        when(cognitoClient.confirmForgotPassword(any(ConfirmForgotPasswordRequest.class)))
                .thenReturn(new ConfirmForgotPasswordResult());

        UserConfirmForgotPasswordRequest request = new UserConfirmForgotPasswordRequest();
        request.setEmail("user@test.com");
        request.setVerification_code("code");
        request.setPassword("newPassword");

        ResponseEntity<?> response = authController.confirmForgotPassword(request);

        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("Password reset successful", ((GenericAuthResponse) response.getBody()).getMessage());

        verify(cognitoClient, times(1)).confirmForgotPassword(any(ConfirmForgotPasswordRequest.class));
    }
}