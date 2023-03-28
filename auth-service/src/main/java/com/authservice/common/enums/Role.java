package com.authservice.common.enums;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("ROLE_ADMIN", "운영자"),
    USER("ROLE_USER", "일반 사용자")

    ;

    private final String auth;
    private final String val;

    Role(String auth, String val) {
        this.auth = auth;
        this.val = val;
    }
}