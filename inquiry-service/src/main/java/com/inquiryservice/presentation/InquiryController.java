package com.inquiryservice.presentation;

import com.inquiryservice.InquiryService;
import com.inquiryservice.common.enums.Period;
import com.inquiryservice.dto.WeatherRequestDto;
import com.inquiryservice.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping("/weather/latest")
    public ResponseEntity<WeatherResponseDto> getLatestWeatherInfo(@RequestParam(name = "p") String parentRegion, @RequestParam(name = "c") String childRegion) {
        WeatherResponseDto weatherResponseDto = inquiryService.findLatestWeatherInfoByRegionName(parentRegion, childRegion);
        return ResponseEntity.ok(weatherResponseDto);
    }

    @GetMapping("/weather/list")
    public ResponseEntity<List<WeatherResponseDto>> getWeatherInfoListByPeriod(
            @RequestParam(name = "p") String parentRegion, @RequestParam(name = "c") String childRegion, @RequestParam(required = false, defaultValue = "DAY") Period period
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = null;

        switch (period) {
            case DAY -> threshold = now.minusDays(1);
            case WEEK -> threshold = now.minusWeeks(1);
            case MONTH -> threshold = now.minusMonths(1);
        }

        List<WeatherResponseDto> weatherResponseDtoList = inquiryService.findWeatherInfoAfterThresholdByRegionName(parentRegion, childRegion, threshold);
        return ResponseEntity.ok(weatherResponseDtoList);
    }

    @PostMapping("/weathers")
    public ResponseEntity<String> saveWeatherList(@RequestBody List<WeatherRequestDto> weatherRequestDtoList) {
        inquiryService.saveWeatherList(weatherRequestDtoList);
        return new ResponseEntity<>("날씨 리스트 저장 완료", HttpStatus.CREATED);
    }
}