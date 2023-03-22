package com.inquiryservice.common.enums;

import lombok.Getter;

@Getter
public enum Period {

    DAY("하루"),
    WEEK("일주일"),
    MONTH("한달");

    private final String val;

    Period(String val) {
        this.val = val;
    }
}