package com.inquiryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRequestDto {

    private String parentRegion;
    private String childRegion;
    private int nx;
    private int ny;
    private String baseDate;
    private String baseTime;
    private double temperature;
    private double rainfall;
    private double humid;
}