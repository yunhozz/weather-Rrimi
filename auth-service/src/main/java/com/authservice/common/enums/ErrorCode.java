package com.authservice.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTER_SERVER_ERROR(500, "내부 서버 에러가 발생했습니다."),
    NOT_VALID(400, "요청 필드에 대한 검증에 실패하였습니다."),
    USER_NOT_FOUND(404, "해당 유저를 찾을 수 없습니다."),
    REDIS_DATA_NOT_FOUND(404, "해당 key 에 대한 redis 데이터가 존재하지 않습니다."),
    EMAIL_DUPLICATE(400, "중복되는 이메일이 존재합니다."),
    PASSWORD_DIFFERENT(400, "기존 비밀번호와 입력하신 비밀번호가 일치하지 않습니다."),
    PASSWORD_UPDATE_FAIL(400, "변경하려는 비밀번호를 정확히 입력해주세요."),

    ;

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}