package com.mybudget.domain.service;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.user.User;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.domain.service.validation.ValidateUserRegister;
import com.mybudget.infra.exception.UserRegisterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidateUserRegister validateUserRegister;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(UserDto userDto) {

        try {

            validateUserRegister.validate(userDto);

            String password = passwordEncoder.encode(userDto.password());

            User user = new User();
            user.setName(userDto.name());
            user.setEmail(userDto.email());
            user.setPassword(password);

            User newUser = userRepository.save(user);

            return new UserDto(newUser);

        } catch (UserRegisterException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ocorreu um erro inesperado. Por favor, tente novamente.");
        }
    }
}