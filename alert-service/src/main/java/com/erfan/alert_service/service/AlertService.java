package com.erfan.alert_service.service;

import com.erfan.alert_service.repository.AlertRepository;
import kafka.event.AlertingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertService {
    private final EmailService emailService;

    public AlertService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "energy-alerts" ,groupId = "alert-service")
    public void energyUsageAlertEvent(AlertingEvent alertingEvent){
        log.info("Received alerting event {}",alertingEvent);

        final String subject ="Energy Usage Alert" + alertingEvent.getUserId();

        final String message="Alert" + alertingEvent.getMessage() +
                "\n Threshold: " + alertingEvent.getThreshold() +
                "\nEnergy Consumed: " +alertingEvent.getEnergyConsumed();
        emailService.sendEmail(alertingEvent.getEmail(),subject,message,alertingEvent.getUserId());
    }
}
