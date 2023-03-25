package com.batchserver.config;

import com.batchserver.RegionRepository;
import com.batchserver.domain.Region;
import com.batchserver.dto.MailRequestDto;
import com.batchserver.dto.WeatherRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final RegionRepository regionRepository;
    private final RestTemplate restTemplate;

    @Value("${app.weather.secretKey}")
    private String secretKey;

    @Bean
    public Job weatherJob() {
        return new JobBuilder("weatherJob", jobRepository)
                .start(getWeatherInfoStep())
                    .on("FAILED")
                    .to(alertErrorStep())
                .from(getWeatherInfoStep())
                    .on("UNKNOWN")
                    .stopAndRestart(getWeatherInfoStep())
                .from(getWeatherInfoStep())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step getWeatherInfoStep() {
        return new StepBuilder("getWeatherInfoStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddkkmm"));
                    String nowDate = now.substring(0, 8); // 현재 날짜
                    String nowTime = now.substring(8); // 현재 시각

                    JSONParser jsonParser = new JSONParser();
                    List<WeatherRequestDto> weatherRequestDtoList = new ArrayList<>();

                    // id 값이 1 ~ 228 인 region 에 관한 날씨 정보 저장
                    for (long id = 1; id <= 228; id++) {
                        log.info("id = " + id);
                        Region region = regionRepository.getReferenceById(id);

                        // 단기 예보 조회
                        String str = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst" +
                                "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + secretKey +
                                "&" + URLEncoder.encode("base_date", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(nowDate, StandardCharsets.UTF_8) + // 조회하고 싶은 날짜
                                "&" + URLEncoder.encode("base_time", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(nowTime, StandardCharsets.UTF_8) + // 조회하고 싶은 시간
                                "&" + URLEncoder.encode("nx", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(region.getNx()), StandardCharsets.UTF_8) + // 경도
                                "&" + URLEncoder.encode("ny", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(String.valueOf(region.getNy()), StandardCharsets.UTF_8) + // 위도
                                "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("60", StandardCharsets.UTF_8) + // 한 페이지 결과 수
                                "&" + URLEncoder.encode("dataType", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("json", StandardCharsets.UTF_8); // 데이터 타입

                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
                        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
                        ResponseEntity<String> httpResponse = restTemplate.exchange(URI.create(str), HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});
                        JSONObject response = null;

                        try {
                            JSONObject jsonObject = (JSONObject) jsonParser.parse(httpResponse.getBody()); // 날씨 데이터
                            response = (JSONObject) jsonObject.get("response");
                        // 파싱에 실패했을 때
                        } catch (ParseException e) {
                            log.error(e.getMessage());
                            contribution.setExitStatus(ExitStatus.FAILED);
                        // 데이터가 null 일 때
                        } catch (NullPointerException e) {
                            log.error(e.getMessage());
                            contribution.setExitStatus(ExitStatus.UNKNOWN);
                            return RepeatStatus.FINISHED;
                        }

                        JSONObject body = (JSONObject) response.get("body");
                        JSONObject items = (JSONObject) body.get("items");
                        JSONArray item = (JSONArray) items.get("item");

                        String baseDate = (String) ((JSONObject) item.get(0)).get("baseDate");
                        String baseTime = (String) ((JSONObject) item.get(0)).get("baseTime");
                        double temperature = 0;
                        double rainfall = 0;
                        double humid = 0;

                        for (Object data : item) {
                            String category = (String) ((JSONObject) data).get("category");
                            double obsrValue = Double.parseDouble(String.valueOf(((JSONObject) data).get("obsrValue")));

                            switch (category) {
                                case "T1H" -> temperature = obsrValue;
                                case "RN1" -> rainfall = obsrValue;
                                case "REH" -> humid = obsrValue;
                            }
                        }

                        WeatherRequestDto weatherRequestDto = WeatherRequestDto.builder()
                                .parentRegion(region.getParentRegion())
                                .childRegion(region.getChildRegion())
                                .nx(region.getNx())
                                .ny(region.getNy())
                                .baseDate(baseDate)
                                .baseTime(baseTime)
                                .temperature(temperature)
                                .rainfall(rainfall)
                                .humid(humid)
                                .build();

                        weatherRequestDtoList.add(weatherRequestDto);
                    }

                    try {
                        restTemplate.postForEntity("http://localhost:8000/api/query/weathers", weatherRequestDtoList, String.class);
                    // 클라이언트 응답이 없을 때
                    } catch (RestClientException e) {
                        log.error(e.getMessage());
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }

                    return RepeatStatus.FINISHED;

                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step alertErrorStep() {
        return new StepBuilder("alertErrorStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    alertErrorByEmail(); // 이메일 알림
//                    alertErrorBySlack(); // 슬랙 알림
                    return RepeatStatus.FINISHED;

                }, platformTransactionManager)
                .build();
    }

    private void alertErrorByEmail() {
        MailRequestDto mailRequestDto = new MailRequestDto("qkrdbsgh1121@naver.com", "배치 실행 도중 문제가 발생하였습니다. 로그를 확인해주세요.");
        restTemplate.postForEntity("http://localhost:8000/api/alerts/mail", mailRequestDto, String.class);
    }

    private void alertErrorBySlack() {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/alerts/slack")
                .queryParam("errMsg", "배치 실행 도중 문제가 발생하였습니다. 로그를 확인해주세요.")
                .build().toUri();
        restTemplate.postForEntity(uri, null, String.class);
    }

    // TODO: 2023/03/24 날씨 예측 정보 카카오톡으로 매시간 전송
}