package com.authservice.common.enums;

import lombok.Getter;

@Getter
public enum Provider {

    LOCAL("로컬 사용자"),
    GOOGLE("구글 사용자"),
    KAKAO("카카오 사용자"),
    NAVER("네이버 사용자")

    ;

    private final String val;

    Provider(String val) {
        this.val = val;
    }
}