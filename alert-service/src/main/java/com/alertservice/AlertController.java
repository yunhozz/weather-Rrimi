package com.alertservice;

import com.alertservice.dto.MailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/mail")
    public ResponseEntity<String> sendErrorAlertByEmail(@RequestBody MailRequestDto mailRequestDto) {
        alertService.alertErrorByEmail(mailRequestDto);
        return new ResponseEntity<>("에러 알림 이메일이 전송되었습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/slack")
    public ResponseEntity<String> sendErrorAlertBySlack(@RequestParam String errMsg) {
        alertService.alertErrorBySlack(errMsg);
        return new ResponseEntity<>("에러 알림 슬랙 메시지가 전송되었습니다.", HttpStatus.CREATED);
    }
}