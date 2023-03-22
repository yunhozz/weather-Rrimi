package com.batchserver.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    DB_BACKUP_FAIL(500, "DB 백업에 실패하였습니다."),
    PROCESSING_ERROR(500, "CMD 실행 관련 오류가 발생하였습니다."),
    REGION_INIT_FAIL(400, "지역 리스트 초기화에 실패하였습니다."),

    ;

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}