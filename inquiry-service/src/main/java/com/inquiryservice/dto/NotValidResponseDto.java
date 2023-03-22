package com.inquiryservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

@Getter
@NoArgsConstructor
public class NotValidResponseDto {

    private String field;
    private String message;
    private Object rejectedValue;

    public NotValidResponseDto(FieldError fieldError) {
        this.field = fieldError.getField();
        this.message = fieldError.getDefaultMessage();
        this.rejectedValue = fieldError.getRejectedValue();
    }
}