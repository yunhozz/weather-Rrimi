package com.authservice.application.exception;

import com.authservice.common.enums.ErrorCode;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}