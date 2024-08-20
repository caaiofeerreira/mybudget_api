package com.mybudget.domain.dto;

import com.mybudget.domain.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateExpenseDto(@NotNull BigDecimal amount,
                               @NotBlank String description,
                               @NotBlank Status status) {
}