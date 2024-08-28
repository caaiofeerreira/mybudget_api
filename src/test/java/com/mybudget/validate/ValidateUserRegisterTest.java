package com.mybudget.validate;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.domain.service.validation.ValidateUserRegister;
import com.mybudget.domain.user.User;
import com.mybudget.infra.exception.UserRegisterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidateUserRegisterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ValidateUserRegister validateUserRegister;

    @Test
    @DisplayName("Deve lançar UserRegisterException para e-mail com formato inválido")
    public void testValidate_InvalidEmailFormat() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "invalid-email", "123456");

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("O e-mail fornecido é inválido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserRegisterException quando o e-mail é nulo")
    public void testValidate_EmailNull() {

        UserDto userDto = new UserDto(null,"Joao Pedro", null, "123456");

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("O e-mail fornecido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserRegisterException quando o e-mail é vazio")
    public void testValidate_EmailEmpty() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "", "securepassword");

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("O e-mail fornecido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserRegisterException quando o e-mail já existe")
    public void testValidate_EmailAlreadyExists() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "joao@email.com", "123456");

        when(userRepository.existingEmail(userDto.email())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("O e-mail fornecido já está associado a uma conta existente.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserRegisterException para senha menor que 6 digitos")
    public void testValidate_ShortPassword() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "joao@email.com", "12345");

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("A senha deve ter 6 ou mais dígitos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserRegisterException quando a senha é vazia")
    public void testValidate_EmptyPassword() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "joao@email.com", "");

        Exception exception = assertThrows(UserRegisterException.class, () -> validateUserRegister.validate(userDto));
        assertEquals("O password fornecido nao pode ser vazio.", exception.getMessage());
    }
}