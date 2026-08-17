package com.erfan.usage_service.service;

import com.erfan.usage_service.client.DeviceClient;
import com.erfan.usage_service.client.UserClient;
import com.erfan.usage_service.dto.DeviceDto;
import com.erfan.usage_service.dto.UserDto;
import com.erfan.usage_service.model.DeviceEnergy;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import kafka.event.AlertingEvent;
import kafka.event.EnergyUsageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsageService {

    private final InfluxDBClient influxDBClient;
    private final DeviceClient deviceClient;
    private final UserClient userClient;
    private final KafkaTemplate<String, AlertingEvent> kafkaTemplate;

    @Value("${influx.bucket}")
    private String bucket;

    @Value("${influx.org}")
    private String influxOrg;

    public UsageService(InfluxDBClient influxDBClient,
                        DeviceClient deviceClient,
                        UserClient userClient,
                        KafkaTemplate<String, AlertingEvent> kafkaTemplate) {
        this.influxDBClient = influxDBClient;
        this.deviceClient = deviceClient;
        this.userClient = userClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "energy-usage", groupId = "usage-service")
    public void handleEnergyUsageEvent(EnergyUsageEvent energyUsageEvent) {
        try {
            Point point = Point.measurement("energy-usage") // Keep hyphen consistent
                    .addTag("deviceId", String.valueOf(energyUsageEvent.deviceId()))
                    .addField("energyConsumed", energyUsageEvent.energyConsumed())
                    .time(energyUsageEvent.timestamp(), WritePrecision.MS);

            influxDBClient.getWriteApiBlocking().writePoint(bucket, influxOrg, point);
        } catch (Exception e) {
            log.error("Failed to write to InfluxDB for device: {}", energyUsageEvent.deviceId(), e);
        }
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void aggregateDeviceEnergyUsage() {
        final Instant now = Instant.now();
        final Instant oneHourAgo = now.minusSeconds(3600);

        // FIX 1: Changed "energy_usage" to "energy-usage" to match the write measurement
        String fluxQuery = String.format("""
                from(bucket: "%s")
                  |> range(start: time(v: "%s"), stop: time(v: "%s"))
                  |> filter(fn: (r) => r["_measurement"] == "energy-usage")
                  |> filter(fn: (r) => r["_field"] == "energyConsumed")
                  |> group(columns: ["deviceId"])
                  |> sum(column: "_value")
                """, bucket, oneHourAgo.toString(), now.toString());

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);
        List<DeviceEnergy> deviceEnergies = new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                String deviceIdStr = (String) record.getValueByKey("deviceId");
                Object value = record.getValueByKey("_value");

                // FIX 2: Cleaner null handling for the aggregated value
                double energyConsumed = value != null ? ((Number) value).doubleValue() : 0.0;

                if (deviceIdStr != null) {
                    deviceEnergies.add(DeviceEnergy.builder()
                            .deviceId(Long.valueOf(deviceIdStr))
                            .energyConsumed(energyConsumed)
                            .build());
                }
            }
        }

        log.info("Aggregated device energies over the past hour: {}", deviceEnergies);

        // Enrich device data with User ID
        for (DeviceEnergy deviceEnergy : deviceEnergies) {
            try {
                DeviceDto deviceResponse = deviceClient.getDeviceById(deviceEnergy.getDeviceId());
                if (deviceResponse == null || deviceResponse.id() == null) {
                    log.warn("Device not found for id: {}", deviceEnergy.getDeviceId());
                    continue;
                }
                deviceEnergy.setUserId(deviceResponse.userId());
            } catch (Exception e) {
                // FIX 3: Typo fix ("fatch" -> "fetch")
                log.warn("Failed to fetch device for id: {}", deviceEnergy.getDeviceId(), e);
            }
        }

        // Remove devices with null userId
        deviceEnergies.removeIf(de -> de.getUserId() == null);

        // Group by User ID
        Map<Long, List<DeviceEnergy>> userDeviceEnergyMap = deviceEnergies.stream()
                .collect(Collectors.groupingBy(DeviceEnergy::getUserId));

        log.info("User-device Energy map: {}", userDeviceEnergyMap);

        // Fetch user thresholds and emails
        List<Long> userIds = new ArrayList<>(userDeviceEnergyMap.keySet());
        final Map<Long, Double> userThresholdMap = new HashMap<>(); // FIX 4: Typo fix
        final Map<Long, String> userEmailMap = new HashMap<>();

        for (final Long userId : userIds) {

            try {

                UserDto user = userClient.getUserById(userId);

                log.info("User response for userId {}: {}", userId, user);

                if (user == null) {
                    log.warn("User not found for id: {}", userId);
                    continue;
                }

                if (user.email() == null) {
                    log.warn("User email is missing for id: {}", userId);
                    continue;
                }

                if (!user.alerting()) {
                    log.info("Alerting is disabled for user id: {}", userId);
                    continue;
                }

                userThresholdMap.put(
                        userId,
                        user.energyAlertingThreshold()
                );

                userEmailMap.put(
                        userId,
                        user.email()
                );

            } catch (Exception e) {

                log.warn(
                        "Failed to fetch user for id: {}",
                        userId,
                        e
                );
            }
        }

        log.info("User threshold map: {}", userThresholdMap);
        // Check threshold against aggregated usage
        // FIX 6: Iterate directly over the map entries for cleaner code
        for (Map.Entry<Long, Double> entry : userThresholdMap.entrySet()) {
            Long userId = entry.getKey();
            Double threshold = entry.getValue();
            List<DeviceEnergy> devices = userDeviceEnergyMap.get(userId);

            if (devices == null) continue;

            double totalConsumption = devices.stream()
                    .mapToDouble(DeviceEnergy::getEnergyConsumed)
                    .sum();

            if (totalConsumption > threshold) {
                // FIX 7: Fixed SLF4J string concatenation bug
                log.info("ALERT: User Id {} has exceeded the energy threshold. Total consumption: {}, Threshold: {}",
                        userId, totalConsumption, threshold);

                AlertingEvent alertingEvent = AlertingEvent.builder()
                        .userId(userId)
                        .message("Energy consumption threshold exceeded")
                        .threshold(threshold)
                        .energyConsumption(totalConsumption)
                        .email(userEmailMap.get(userId))
                        .build();

                kafkaTemplate.send("energy-alerts", alertingEvent);
            } else {
                log.debug("User Id {} is within the energy threshold. Total consumption: {}, Threshold: {}",
                        userId, totalConsumption, threshold);
            }
        }
    }
}