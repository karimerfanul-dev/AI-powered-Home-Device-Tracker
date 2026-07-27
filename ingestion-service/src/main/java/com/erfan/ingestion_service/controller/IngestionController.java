package com.erfan.ingestion_service.controller;

import com.erfan.ingestion_service.dto.EnergyUsageDto;
import com.erfan.ingestion_service.service.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion")
public class IngestionController {
    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionservice){
        this.ingestionService=ingestionservice;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ingestData(@RequestBody EnergyUsageDto usageDto){
        ingestionService.ingestEnergyUsage(usageDto);
    }
}
