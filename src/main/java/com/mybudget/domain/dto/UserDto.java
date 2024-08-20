package com.mybudget.domain.dto;

import com.mybudget.domain.model.User;

public record UserDto(Long id,
                      String name,
                      String email,
                      String password) {

    public UserDto(User user) {
        this(user.getId(),
                user.getName(),
                user.getName(),
                user.getPassword());
    }
}
