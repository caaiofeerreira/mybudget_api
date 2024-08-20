package com.mybudget.domain.service;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.model.User;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.infra.exception.UnauthorizedAccessException;
import com.mybudget.infra.exception.UserRegisterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRegister {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto register(UserDto userDto) {

        try {

            validate(userDto);

            String password = passwordEncoder.encode(userDto.password());

            User user = new User();
            user.setName(userDto.name());
            user.setEmail(userDto.email());
            user.setPassword(password);

            User newUser = userRepository.save(user);

            return new UserDto(newUser);

        } catch (UserRegisterException e) {
            throw new UnauthorizedAccessException("Erro ao registrar sua conta. " + e.getMessage());
        }
    }

    private void validate(UserDto userDto) {

        Optional<User> existingClient = userRepository.existingEmail(userDto.email());

        existingClient.ifPresent(user -> {
            throw new UserRegisterException("O e-mail fornecido já está associado a uma conta existente.");
        });
    }
}