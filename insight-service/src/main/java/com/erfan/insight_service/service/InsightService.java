package com.erfan.insight_service.service;

import com.erfan.insight_service.client.UsageClient;
import com.erfan.insight_service.dto.DeviceDto;
import com.erfan.insight_service.dto.InsightDto;
import com.erfan.insight_service.dto.UsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InsightService {

    private final UsageClient usageClient;
    private final OllamaChatModel ollamaChatModel;

    public InsightService (UsageClient usageClient, OllamaChatModel ollamaChatModel) {
        this.usageClient=usageClient;
        this.ollamaChatModel=ollamaChatModel;
    }
    public InsightDto getSavingsTips(Long userId) {
        //fetch data from Usage service
        final UsageDto usageData=usageClient.getxDaysUsageForUser(userId,3);

        double totalUsage=usageData.devices().stream()
                .mapToDouble((DeviceDto::energyConsumed))
                .sum();
        log.info("Calling Ollama for userId {} with total usage {} ",userId,totalUsage);

        String prompt=new StringBuilder()
                .append("This is my total consumption over past 3 days." )
                .append("How i can reduce my energy consumption? how does it compare to average household.")
                .append("Total energy used: \n")
                .append(totalUsage)
                .toString();

        ChatResponse response=ollamaChatModel.call(
                Prompt.builder()
                        .content(prompt)
                        .build());

        return InsightDto.builder()
                .userId(userId)
                .tips(response.getResult().getOutput().getText())
                .energyUsage(totalUsage)
                .build();
    }

    public InsightDto getOverview(Long userId) {
         //fetch data from Usage service
        final UsageDto usageData=usageClient.getxDaysUsageForUser(userId,3);

        double totalUsage=usageData.devices().stream()
                .mapToDouble((DeviceDto::energyConsumed))
                .sum();
        log.info("Calling Ollama for userId {} with total usage {} ",userId,totalUsage);

        String prompt=new StringBuilder()
                .append("Analyse the following energy usage data and provide a" +
                "concise overview with actionable insights." )
                .append("this data is the aggregate data for the past 3 days. ")
                .append("Usage data: \n")
                .append(usageData.devices())
                .toString();

        ChatResponse response=ollamaChatModel.call(
                Prompt.builder()
                        .content(prompt)
                        .build());

        return InsightDto.builder()
                .userId(userId)
                .tips(response.getResult().getOutput().getText())
                .energyUsage(totalUsage)
                .build();
    }
}
