package com.mybudget.controller;

import com.mybudget.domain.user.User;
import com.mybudget.domain.user.UserAuthentication;
import com.mybudget.infra.exception.InvalidCredentialsException;
import com.mybudget.infra.security.DadosTokenJWT;
import com.mybudget.infra.security.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mybudget")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid UserAuthentication dados) {

        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.password());
            var authentication = manager.authenticate(authenticationToken);
            var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());

            return ResponseEntity.status(HttpServletResponse.SC_OK).body(new DadosTokenJWT(tokenJWT));
        }
        catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Credenciais inválidas fornecidas. Verifique email e password e tente novamente.");
        }
    }
}