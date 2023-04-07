package com.authservice.application.exception;

import com.authservice.common.enums.ErrorCode;

public class PasswordDifferentException extends AuthException {

    public PasswordDifferentException() {
        super(ErrorCode.PASSWORD_DIFFERENT);
    }
}