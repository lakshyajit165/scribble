package com.scribble.authservice.constants;

public final class URLConstants {
    public static final String SIGNUP_URL = "/auth-service/api/v1/auth/sign_up";
    public static final String SIGNIN_URL = "/auth-service/api/v1/auth/sign_in";
    public static final String CONFIRM_PASSWORD_URL = "/auth-service/api/v1/auth/confirm_password";
    public static final String FORGOT_PASSWORD_URL = "/auth-service/api/v1/auth/forgot_password";
    public static final String CONFIRM_FORGOT_PASSWORD_URL = "/auth-service/api/v1/auth/confirm_forgot_password";
    public static final String GET_PROTECTED_RESOURCE = "/auth-service/api/v1/auth/resource";
    public static final String GET_PROTECTED_RESOURCE2 = "/auth-service/api/v1/auth/resource2";
    public static final String LOGOUT_URL = "/auth-service/api/v1/auth/logout";
    public static final String GET_NEW_CREDS_URL = "/auth-service/api/v1/auth/get_new_creds";
    public static final String TEST_URL = "/auth-service/api/v1/auth/test";

    private URLConstants(){

    }
}
