package com.mybudget.service;

import com.mybudget.domain.dto.UserDto;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.domain.service.UserRegisterService;
import com.mybudget.domain.service.validation.ValidateUserRegister;
import com.mybudget.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegisterService userRegisterService;

    @Test
    @DisplayName("Deve registrar um usuário com sucesso")
    public void testUserRegister_Success() {

        UserDto userDto = new UserDto(null, "Joao Pedro", "joao@email.com", "123456");

        User user = new User();
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setPassword("encoded_password");

        when(passwordEncoder.encode(userDto.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userRegisterService.register(userDto);

        assertNotNull(result);
        assertEquals(userDto.name(), result.name());
        assertEquals(userDto.email(), result.email());
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando ocorrer um erro inesperado ao registrar um usuário")
    public void testUserRegister_Exception() {

        UserDto userDto = new UserDto(null,"Joao Pedro", "joao@email.com", "123456");

        when(passwordEncoder.encode(userDto.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Ocorreu um erro inesperado. Por favor, tente novamente."));

        Exception exception = assertThrows(RuntimeException.class, () -> userRegisterService.register(userDto));
        assertTrue(exception.getMessage().contains("Ocorreu um erro inesperado. Por favor, tente novamente."));
    }
}