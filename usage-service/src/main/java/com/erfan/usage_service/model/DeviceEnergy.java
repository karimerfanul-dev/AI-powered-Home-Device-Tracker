package com.erfan.usage_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEnergy {
   private Long deviceId;
    private double energyConsumed;
    private Long userId;
}
