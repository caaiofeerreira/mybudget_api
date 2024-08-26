package com.mybudget.domain.dto;

import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;

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