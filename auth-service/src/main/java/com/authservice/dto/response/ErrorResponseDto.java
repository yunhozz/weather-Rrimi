package com.authservice.dto.response;

import com.authservice.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private Integer code;
    private String message;
    private List<NotValidResponseDto> notValidList;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.timestamp = LocalDateTime.now();
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ErrorResponseDto(ErrorCode errorCode, List<NotValidResponseDto> notValidList) {
        this.timestamp = LocalDateTime.now();
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
        this.notValidList = notValidList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotValidResponseDto {

        private String field;
        private Object rejectedValue;
        private String message;
    }
}