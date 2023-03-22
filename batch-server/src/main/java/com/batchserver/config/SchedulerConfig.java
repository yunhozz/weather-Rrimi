package com.batchserver.config;

import com.batchserver.dto.MailRequestDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    private final ThreadPoolTaskScheduler taskScheduler;

    public SchedulerConfig() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setErrorHandler(throwable -> {
            RestTemplate template = new RestTemplate();
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));

            alertErrorByEmail(template, sw); // 이메일 알림
//            alertErrorBySlack(template, sw); // 슬랙 알림
        });

        taskScheduler.initialize();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
    }

    private void alertErrorByEmail(RestTemplate template, StringWriter sw) {
        MailRequestDto mailRequestDto = new MailRequestDto("qkrdbsgh1121@naver.com", sw.toString());
        template.postForEntity("http://localhost:8000/api/alerts/mail", mailRequestDto, String.class);
    }

    private void alertErrorBySlack(RestTemplate template, StringWriter sw) {
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/alerts/slack")
                .queryParam("errMsg", sw.toString())
                .build().toUri();
        template.postForEntity(uri, null, String.class);
    }
}