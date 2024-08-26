package com.mybudget.domain.service.validation;

import com.mybudget.domain.dto.ExpenseDto;
import com.mybudget.infra.exception.ExpenseProcessingException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValidateNewExpense {

    public void validate(ExpenseDto expenseDto) {

        if (expenseDto.amount() == null || expenseDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExpenseProcessingException("O valor da despesa deve ser maior que zero.");
        }

        if (expenseDto.description() == null || expenseDto.description().trim().isEmpty()) {
            throw new ExpenseProcessingException("A descrição da despesa não pode estar vazia.");
        }
    }
}