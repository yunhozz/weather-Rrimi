package com.authservice.application.exception;

import com.authservice.common.enums.ErrorCode;

public class RedisDataNotFoundException extends AuthException {

    public RedisDataNotFoundException() {
        super(ErrorCode.REDIS_DATA_NOT_FOUND);
    }
}