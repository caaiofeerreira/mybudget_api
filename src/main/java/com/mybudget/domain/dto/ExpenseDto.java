package com.mybudget.domain.dto;

import com.mybudget.domain.model.Expense;
import com.mybudget.domain.model.Status;
import com.mybudget.domain.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseDto(UUID id,
                         String description,
                         BigDecimal amount,
                         Status status,
                         LocalDate date) {

    public ExpenseDto(Expense expense) {
        this(expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getStatus(),
                expense.getDate());
    }
}