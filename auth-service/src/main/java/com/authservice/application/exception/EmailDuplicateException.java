package com.authservice.application.exception;

import com.authservice.common.enums.ErrorCode;

public class EmailDuplicateException extends AuthException {

    public EmailDuplicateException() {
        super(ErrorCode.EMAIL_DUPLICATE);
    }
}