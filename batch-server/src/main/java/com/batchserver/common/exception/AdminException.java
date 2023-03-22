package com.batchserver.common.exception;

import com.batchserver.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AdminException extends RuntimeException {

    private final ErrorCode errorCode;

    public AdminException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}