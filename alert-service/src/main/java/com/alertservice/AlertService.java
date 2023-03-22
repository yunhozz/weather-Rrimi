package com.alertservice;

import com.alertservice.dto.MailRequestDto;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final JavaMailSender mailSender;

    public void alertErrorByEmail(MailRequestDto mailRequestDto) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setRecipients(Message.RecipientType.TO, mailRequestDto.getEmail());
            message.setSubject("애플리케이션 에러 알림입니다.");
            message.setText(mailRequestDto.getErrMsg());
            mailSender.send(message);
            log.info("Mail Send at " + new Date());

        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("메일 전송에 실패하였습니다.");
        }
    }

    public void alertErrorBySlack(String errMsg) {
        SlackAttachment slackAttachment = new SlackAttachment();
        slackAttachment.setFallback("Error");
        slackAttachment.setColor("danger");
        slackAttachment.setTitle("Error Detect");
        slackAttachment.setText(errMsg);
        slackAttachment.setColor("danger");

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setAttachments(Collections.singletonList(slackAttachment));
        slackMessage.setIcon(":ghost:");
        slackMessage.setText("Error Information");
        slackMessage.setUsername("DutyPark");

        SlackApi slackApi = new SlackApi("https://hooks.slack.com/services/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");
        slackApi.call(slackMessage);
    }
}