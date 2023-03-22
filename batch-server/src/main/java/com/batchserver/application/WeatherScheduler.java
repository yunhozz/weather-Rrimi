package com.batchserver.application;

import com.batchserver.config.BatchConfig;
import com.batchserver.common.exception.DbBackupFailException;
import com.batchserver.common.exception.ProcessingErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final JobLauncher jobLauncher;
    private final BatchConfig batchConfig;

    @Value("${app.file.directory}")
    private String fileDir;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // 단기 예보 조회: batch + scheduler
    @Scheduled(cron = "0 35 * * * ?") // 매시 35분마다 실행
    public void getWeatherInfoByCallOpenApi() {
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(new Date(), Date.class));
        JobParameters jobParameters = new JobParameters(parameters);

        try {
            log.info(">>>>>>>>>> Getting Weather Information Started at " + new Date());
            jobLauncher.run(batchConfig.weatherJob(), jobParameters);
            log.info("<<<<<<<<<< Saving Weather Information Job Finished at " + new Date());

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getMessage());
        }
    }

    // DB 롤백: scheduler
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void dbBackup() {
        log.info(">>>>>>>>>> DB Backup Started at " + new Date());
        String backupDateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filePath = fileDir + "backup/";
        String fileName = "DB_backup_" + backupDateStr + ".sql";
        String saveName = filePath + fileName;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/opt/homebrew/bin/mysqldump", "-u", "root", "-p" + dbPassword, "weather_app", ">" + saveName);
            Process process = processBuilder.start();
            InputStream is = process.getInputStream();

            int read;
            while ((read = is.read()) != -1) {
                System.out.print((char) read);
            }

            int exitStatus = process.waitFor();
            if (exitStatus == 0) {
                log.info("<<<<<<<<<< DB Backup Complete at " + new Date());
            } else {
                log.error("<<<<<<<<<< DB Backup Failure!!");
                throw new DbBackupFailException();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ProcessingErrorException();
        }
    }
}