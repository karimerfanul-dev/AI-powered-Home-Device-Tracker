package com.erfan.user_service.service;

import com.erfan.user_service.dto.UserDto;
import com.erfan.user_service.enitity.User;
import com.erfan.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserDto createUser(UserDto input){
        log.info("createUser: {}",input);
        final User createdUser = User.builder()
                .name(input.getName())
                .lastname(input.getLastname())
                .email(input.getEmail())
                .id(input.getId())
                .address(input.getAddress())
                .alerting(input.isAlerting())
                .energyAlertingThreshold(input.getEnergyAlertingThreshold())
                .build();
        final User savedUser = userRepository.save(createdUser);
        return toDto(savedUser);
    }

    private UserDto toDto(User savedUser){
        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .lastname(savedUser.getLastname())
                .email(savedUser.getEmail())
                .address(savedUser.getAddress())
                .alerting(savedUser.isAlerting())
                .energyAlertingThreshold(savedUser.getEnergyAlertingThreshold())
                .build();
    }
}
