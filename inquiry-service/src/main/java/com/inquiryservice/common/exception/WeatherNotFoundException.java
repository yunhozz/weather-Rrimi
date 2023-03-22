package com.inquiryservice.common.exception;

public class WeatherNotFoundException extends InquiryException {

    public WeatherNotFoundException() {
        super("해당하는 지역의 날씨 정보가 존재하지 않습니다.");
    }
}