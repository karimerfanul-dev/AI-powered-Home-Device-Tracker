package com.erfan.user_service.service;

import com.erfan.user_service.dto.UserDto;
import com.erfan.user_service.enitity.User;
import com.erfan.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserDto createUser(UserDto input){
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

    public UserDto getUserById(@PathVariable Long id){

        return userRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public void updateUser(Long id, UserDto userDto) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("User with id: "+id+" not found"));

        user.setName(userDto.getName());
        user.setLastname(userDto.getLastname());
        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setAlerting(userDto.isAlerting());
        user.setEnergyAlertingThreshold(userDto.getEnergyAlertingThreshold());
        userRepository.save(user);
    }

    public void delectUser(Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("User with id: "+id+" not found"));
        userRepository.delete(user);
    }
}
