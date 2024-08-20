package com.mybudget.infra.exception;

public class UserRegisterException extends RuntimeException {

    public UserRegisterException(String message) {
        super(message);
    }
}