package com.inquiryservice.dto;

import com.inquiryservice.domain.Weather;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDto {

    private String baseDate;
    private String baseTime;
    private int nx;
    private int ny;
    private double temperature;
    private double rainfall;
    private double humid;

    public WeatherResponseDto(Weather weather) {
        this.baseDate = weather.getBaseDate();
        this.baseTime = weather.getBaseTime();
        this.nx = weather.getRegionInfo().getNx();
        this.ny = weather.getRegionInfo().getNy();
        this.temperature = weather.getTemperature();
        this.rainfall = weather.getRainfall();
        this.humid = weather.getHumid();
    }
}