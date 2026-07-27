package com.erfan.ingestion_service.simulation;

import com.erfan.ingestion_service.dto.EnergyUsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Slf4j
@Component
public class ContinuousDataSimulator implements CommandLineRunner {
    private final RestTemplate restTemplate=new RestTemplate();
    private final Random random=new Random();

    @Value("${simulation.requests-per-interval}")
    private int requestPerInterval;

    @Value("${simulation.endpoint}")
    private String ingestionEndpoint;

    @Override
    public void run(String... args) throws Exception {
        log.info("Simulation started");
    }

    //@Scheduled(fixedRateString = "${simulation.interval-ms}")
    public void sendMockData(){

        for(int i=0;i<requestPerInterval;i++){
            EnergyUsageDto energyUsageDto=EnergyUsageDto.builder()
                    .deviceId(random.nextLong(1,3))
                    .energyConsumed(Math.round(random.nextDouble(0.0,10.0)*100.0)/100.0)
                    .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                    .build();
            try{
                HttpHeaders headers=new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<EnergyUsageDto> request=new HttpEntity<>(energyUsageDto,headers);
                restTemplate.postForEntity(ingestionEndpoint,request,Void.class);
                log.info("sent mock data: {}",energyUsageDto);
            }catch(Exception e){
                log.error("failed to sent mock data: {}",e.getMessage());
            }
        }
    }

}
