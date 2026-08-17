package com.erfan.alert_service.repository;

import com.erfan.alert_service.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert,Long> {
}
