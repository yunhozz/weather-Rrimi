package com.authservice.common.enums;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS("atk"),
    REFRESH("rtk")

    ;

    private final String type;

    TokenType(String type) {
        this.type = type;
    }
}