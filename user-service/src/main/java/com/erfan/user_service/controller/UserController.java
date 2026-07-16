package com.erfan.user_service.controller;

import com.erfan.user_service.dto.UserDto;
import com.erfan.user_service.service.UserService;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        UserDto userDto=userService.getUserById(id);
        if(userDto==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto,@PathVariable Long id){
        try{
            userService.updateUser(id,userDto);
            return ResponseEntity.ok("User updated successfully");
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        try {
            userService.delectUser(id);
            return ResponseEntity.ok("User deleted successfully");
        }
        catch(IllegalArgumentException e){
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        }
    }
}
