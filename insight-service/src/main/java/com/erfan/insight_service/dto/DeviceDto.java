package com.erfan.insight_service.dto;

public record DeviceDto(
        long id,
        String name,
        String type,
        String location,
        double energyConsumed
) {
}
