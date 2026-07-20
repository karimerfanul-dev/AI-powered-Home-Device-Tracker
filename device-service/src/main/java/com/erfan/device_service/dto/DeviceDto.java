package com.erfan.device_service.dto;

import com.erfan.device_service.model.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeviceDto {
    private Long id;
    private String name;
    private String location;
    private DeviceType type;
    private Long userId;

}
