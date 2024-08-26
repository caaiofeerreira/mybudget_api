package com.mybudget.domain.service.validation;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.user.User;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.infra.exception.UserRegisterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidateUserRegister {

    @Autowired
    private UserRepository userRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z][a-zA-Z-]*\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public void validate(UserDto userDto) {

        String email = userDto.email();

        if (!validateEmail(email)) {
           throw new UserRegisterException("O e-mail fornecido é inválido.");
        }

        validatePassword(userDto.password());

        Optional<User> existingClient = userRepository.existingEmail(userDto.email());

        existingClient.ifPresent(user -> {
            throw new UserRegisterException("O e-mail fornecido já está associado a uma conta existente.");
        });
    }

    private boolean validateEmail(String email) {

        if (email == null || email.isEmpty()) {
            throw new UserRegisterException("O e-mail fornecido não pode ser nulo ou vazio.");
        }

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private void validatePassword(String password) {

        if (password == null || password.isEmpty()) {
            throw new UserRegisterException("O password fornecido nao pode ser vazio.");
        }

        if (password.length() <= 5) {
            throw new UserRegisterException("A senha deve ter mais de 5 dígitos.");
        }
    }

}