package com.mybudget.domain.service.validation;

import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;
import com.mybudget.domain.user.User;
import com.mybudget.infra.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Component;

@Component
public class ValidateDeleteExpense {

    public void validate(Expense expense, User user) {

        if (expense.getStatus() == Status.PENDING) {
            throw new UnauthorizedAccessException("Você não pode deletar uma despesa que se encontra PENDENTE.");
        }

        if (!expense.getUser().equals(user)) {
            throw new UnauthorizedAccessException("Você não tem permissão para atualizar esta despesa.");
        }
    }
}