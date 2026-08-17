package com.erfan.alert_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="alert")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private boolean sent;
}
