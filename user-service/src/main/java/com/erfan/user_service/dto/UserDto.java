package com.erfan.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String address;
    private boolean alerting;
    private double energyAlertingThreshold;
}
