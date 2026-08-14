package com.erfan.usage_service.client;

import com.erfan.usage_service.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UserClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserClient(@Value("${user.service.url}") String baseUrl){
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }
    public UserDto getUserById(final Long userId){
        String url= UriComponentsBuilder.fromUriString(baseUrl)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();

        ResponseEntity<UserDto> response=restTemplate.getForEntity(url, UserDto.class);
        return response.getBody();
    }
}
