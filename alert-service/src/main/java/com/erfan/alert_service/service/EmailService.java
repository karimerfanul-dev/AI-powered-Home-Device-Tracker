package com.erfan.alert_service.service;

import com.erfan.alert_service.entity.Alert;
import com.erfan.alert_service.repository.AlertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final AlertRepository alertRepository;

    public EmailService(JavaMailSender mailSender,
                        AlertRepository alertRepository) {
        this.mailSender = mailSender;
        this.alertRepository = alertRepository;
    }
    public void sendEmail(String to,
                          String subject,
                          String body,
                          Long userId){
        log.info("Sending email to {} subject: {}",to,subject);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom("noreply@jihad.com");
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        try{
            mailSender.send(mailMessage);

            final Alert alert = Alert.builder()
                    .sent(true)
                    .createdAt(java.time.LocalDateTime.now())
                    .userId(userId)
                    .build();
            alertRepository.saveAndFlush(alert);

        }catch(Exception e){
            log.error("failed to send email to {} ",to,e);
            final Alert alert=Alert.builder()
                    .sent(false)
                    .createdAt(java.time.LocalDateTime.now())
                    .userId(userId)
                    .build();
            alertRepository.saveAndFlush(alert);
        }
        log.info("Email sent {}",to);
    }
}
