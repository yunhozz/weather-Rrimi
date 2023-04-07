package com.authservice.application.exception;

import com.authservice.common.enums.ErrorCode;

public class PasswordUpdateFailException extends AuthException {

    public PasswordUpdateFailException() {
        super(ErrorCode.PASSWORD_UPDATE_FAIL);
    }
}