package com.mybudget.infra.exception;

public class ExpenseProcessingException extends RuntimeException {

    public ExpenseProcessingException(String message) {
        super(message);
    }
}