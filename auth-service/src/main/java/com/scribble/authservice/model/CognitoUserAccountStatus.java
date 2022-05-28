package com.scribble.authservice.model;

public enum CognitoUserAccountStatus {
    UNCONFIRMED,
    CONFIRMED,
    ARCHIVED,
    UNKNOWN, // used here when user is not found in adminGetUser
    FORCE_CHANGE_PASSWORD,
    RESET_REQUIRED
}
