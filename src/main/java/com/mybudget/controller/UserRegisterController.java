package com.mybudget.controller;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.service.UserRegisterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mybudget")
public class UserRegisterController {

    @Autowired
    private UserRegisterService register;

    @PostMapping("/user-register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto userDto) {

        UserDto user = register.register(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}