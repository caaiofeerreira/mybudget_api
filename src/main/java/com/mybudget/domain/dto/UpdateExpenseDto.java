package com.mybudget.domain.dto;

import com.mybudget.domain.expense.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateExpenseDto(@NotNull BigDecimal amount,
                               @NotBlank String description,
                               @NotBlank Status status) {
}