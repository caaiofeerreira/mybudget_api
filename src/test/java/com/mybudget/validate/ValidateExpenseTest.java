package com.mybudget.validate;

import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;
import com.mybudget.domain.service.validation.ValidateDeleteExpense;
import com.mybudget.domain.user.User;
import com.mybudget.infra.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidateExpenseTest {

    @InjectMocks
    private ValidateDeleteExpense validateDeleteExpense;

    @Test
    @DisplayName("Deve lançar UnauthorizedAccessException ao tentar validar uma despesa com status PENDENTE para exclusão")
    public void testDeleteExpense_ValidateStatus() {

        User user = new User();
        user.setId(1L);

        Expense expense = new Expense();
        expense.setStatus(Status.PENDING);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () ->
                validateDeleteExpense.validate(expense, user)
        );
        assertEquals("Você não pode deletar uma despesa que se encontra PENDENTE.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UnauthorizedAccessException ao tentar atualizar uma despesa que nao pertence ao usuario")
    public void testDeleteExpense_ValidateUser() {

        User user = new User();
        user.setId(1L);

        User unauthorizerUser = new User();
        unauthorizerUser.setId(2L);

        Expense expense = new Expense();
        expense.setUser(user);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () ->
                validateDeleteExpense.validate(expense, unauthorizerUser)
        );
        assertEquals("Você não tem permissão para atualizar esta despesa.", exception.getMessage());
    }
}