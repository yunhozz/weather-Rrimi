package com.inquiryservice;

import com.inquiryservice.common.exception.WeatherNotFoundException;
import com.inquiryservice.domain.RegionInfo;
import com.inquiryservice.domain.Weather;
import com.inquiryservice.dto.WeatherRequestDto;
import com.inquiryservice.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final WeatherRepository weatherRepository;

    @Transactional
    public void saveWeatherList(List<WeatherRequestDto> weatherRequestDtoList) {
        List<Weather> weathers = new ArrayList<>();
        for (WeatherRequestDto weatherRequestDto : weatherRequestDtoList) {
            RegionInfo regionInfo = RegionInfo.builder()
                    .parentRegion(weatherRequestDto.getParentRegion())
                    .childRegion(weatherRequestDto.getChildRegion())
                    .nx(weatherRequestDto.getNx())
                    .ny(weatherRequestDto.getNy())
                    .build();

            Weather weather = Weather.builder()
                    .regionInfo(regionInfo)
                    .baseDate(weatherRequestDto.getBaseDate())
                    .baseTime(weatherRequestDto.getBaseTime())
                    .temperature(weatherRequestDto.getTemperature())
                    .rainfall(weatherRequestDto.getRainfall())
                    .humid(weatherRequestDto.getHumid())
                    .build();

            weathers.add(weather);
        }

        weatherRepository.saveAll(weathers);
    }

    // 특정 지역의 최신 날짜 정보 조회
    @Transactional(readOnly = true)
    public WeatherResponseDto findLatestWeatherInfoByRegionName(String parentRegion, String childRegion) {
        Weather weather = weatherRepository.findLatestWeatherInfoByRegionName(parentRegion, childRegion)
                .orElseThrow(WeatherNotFoundException::new);
        return new WeatherResponseDto(weather);
    }

    // 특정 지역의 특정 기간 내 날씨 정보 리스트 조회
    @Transactional(readOnly = true)
    public List<WeatherResponseDto> findWeatherInfoAfterThresholdByRegionName(String parentRegion, String childRegion, LocalDateTime threshold) {
        return new ArrayList<>() {{
            List<Weather> weathers = weatherRepository.findWeatherInfoAfterThresholdByRegionName(parentRegion, childRegion, threshold);
            for (Weather weather : weathers) {
                WeatherResponseDto weatherResponseDto = new WeatherResponseDto(weather);
                add(weatherResponseDto);
            }
        }};
    }
}