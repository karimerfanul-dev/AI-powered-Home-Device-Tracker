package com.erfan.user_service.controller;

import com.erfan.user_service.dto.UserDto;
import com.erfan.user_service.service.UserService;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto){
        UserDto created=userService.createUser(userDto);
        return new ResponseEntity<>(created,HttpStatus.CREATED);

    }
}
