package com.mybudget.service;

import com.mybudget.domain.dto.ExpenseDto;
import com.mybudget.domain.dto.UpdateExpenseDto;
import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;
import com.mybudget.domain.repository.ExpenseRepository;
import com.mybudget.domain.service.ExpenseService;
import com.mybudget.domain.service.validation.ValidateDeleteExpense;
import com.mybudget.domain.service.validation.ValidateNewExpense;
import com.mybudget.domain.user.User;
import com.mybudget.infra.exception.ExpenseNotFoundException;
import com.mybudget.infra.exception.UnauthorizedAccessException;
import com.mybudget.infra.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ValidateNewExpense validateNewExpense;

    @Mock
    private ValidateDeleteExpense validateDeleteExpense;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    @DisplayName("Deve criar uma nova despesa com sucesso")
    public void testNewExpense_Success() {

        String token = "valid-token";
        ExpenseDto expenseDto = new ExpenseDto(null, "Description", BigDecimal.valueOf(100.00), Status.PENDING, LocalDate.now());
        User user = new User();
        Expense expense = new Expense();

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        doNothing().when(validateNewExpense).validate(expenseDto);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        ExpenseDto result = expenseService.newExpense(token, expenseDto);

        assertNotNull(result);
        assertEquals(expenseDto.amount(), result.amount());
        assertEquals(expenseDto.description(), result.description());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o token for inválido")
    public void testNewExpense_Exception() {

        String token = "invalid-token";
        ExpenseDto expenseDto = new ExpenseDto(null, "Description", BigDecimal.valueOf(100.00), Status.PENDING, LocalDate.now());

        when(tokenService.getUserFromToken(token)).thenThrow(new RuntimeException("Token inválido"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseService.newExpense(token, expenseDto);
        });

        assertTrue(exception.getMessage().contains("Erro ao processar despesa."));
    }

    @Test
    @DisplayName("Deve atualizar a despesa com sucesso quando fornecido um token válido e uma despesa existente")
    public void testUpdateExpense_Success() {

        String token = "valid-token";
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseDto updateExpenseDto = new UpdateExpenseDto(BigDecimal.valueOf(150.00), "Updated Description", Status.PENDING);
        User user = new User();
        Expense existingExpense = new Expense();
        existingExpense.setUser(user);
        existingExpense.setAmount(BigDecimal.valueOf(100.00));
        existingExpense.setDescription("Old Description");
        existingExpense.setStatus(Status.PAID);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(existingExpense);

        ExpenseDto result = expenseService.updateExpense(token, expenseId, updateExpenseDto);

        assertNotNull(result);
        assertEquals(updateExpenseDto.amount(), result.amount());
        assertEquals(updateExpenseDto.description(), result.description());
        assertEquals(updateExpenseDto.status(), result.status());
        verify(expenseRepository).save(existingExpense);
    }

    @Test
    @DisplayName("Deve lançar uma exceção ExpenseNotFoundException quando a despesa não for encontrada")
    public void testUpdateExpense_ExpenseNotFound() {

        String token = "valid-token";
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseDto updateExpenseDto = new UpdateExpenseDto(null, "Description", null);

        when(tokenService.getUserFromToken(token)).thenReturn(new User());
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.updateExpense(token, expenseId, updateExpenseDto);
        });

        assertTrue(exception.getMessage().contains("Despesa não encontrada"));
    }

    @Test
    @DisplayName("Deve lançar uma exceção UnauthorizedAccessException quando o usuário tentar atualizar uma despesa que não possui")
    public void testUpdateExpense_UnauthorizedAccess() {

        String token = "invalid-token";
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseDto updateExpenseDto = new UpdateExpenseDto(null, "Description", null);

        User user = new User();
        user.setId(1L);

        User differentUser = new User();
        differentUser.setId(2L);
        Expense existingExpense = new Expense();
        existingExpense.setUser(differentUser);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            expenseService.updateExpense(token, expenseId, updateExpenseDto);
        });

        assertTrue(exception.getMessage().contains("Você não tem permissão para atualizar esta despesa."));
    }

    @Test()
    @DisplayName("Deve deletar a despesa com sucesso quando o usuário está autorizado e a despesa é encontrada")
    public void testDeleteExpense_Success() {

        String token = "valid-token";
        UUID expenseId = UUID.randomUUID();

        User user = new User();
        user.setId(1L);

        Expense expense = new Expense();
        expense.setUser(user);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        doNothing().when(validateDeleteExpense).validate(expense, user);

        expenseService.deleteExpense(token, expenseId);

        verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("Deve lançar ExpenseNotFoundException quando a despesa não for encontrada para exclusão")
    public void testDeleteExpense_ExpenseNotFound() {

        String token = "valid-token";
        UUID expenseId = UUID.randomUUID();
        User user = new User();

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.deleteExpense(token, expenseId);
        });

        assertTrue(exception.getMessage().contains("Despesa não encontrada"));
    }

    @Test
    @DisplayName("Verifica se o método deleteExpense lança UnauthorizedAccessException quando um usuário não autorizado tenta deletar uma despesa.")
    public void testDeleteExpense_UnauthorizedAccess() {

        String token = "invalid-token";
        UUID expenseId = UUID.randomUUID();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(1L);

        User user = new User();
        user.setId(2L);
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setId(expenseId);


        when(tokenService.getUserFromToken(token)).thenReturn(unauthorizedUser);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        doThrow(new UnauthorizedAccessException("Você não tem permissão para deletar esta despesa."))
                .when(validateDeleteExpense).validate(any(Expense.class), any(User.class));

        assertThrows(UnauthorizedAccessException.class, () -> {
            expenseService.deleteExpense(token, expenseId);
        });
    }

    @Test
    @DisplayName("Deve retornar a lista de despesas do usuário com sucesso")
    public void testGetExpenses_Success() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        Expense expense1 = new Expense();
        expense1.setDate(LocalDate.of(2024, 8, 20));

        Expense expense2 = new Expense();
        expense2.setDate(LocalDate.of(2024, 8, 22));

        List<Expense> expenseList = new ArrayList<>();
        expenseList.add(expense1);
        expenseList.add(expense2);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolved(user.getId())).thenReturn(expenseList);

        List<ExpenseDto> result = expenseService.getExpenses(token);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(expense1.getDate(), result.get(0).date());
        assertEquals(expense2.getDate(), result.get(1).date());
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando não houver despesas registradas")
    public void testGetExpenses_ExpenseNotFound() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolved(user.getId())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.getExpenses(token);
        });

        assertTrue(exception.getMessage().contains("Você não tem despesas registradas no momento."));
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando ocorrer um erro inesperado ao listar despesas")
    public void testGetExpenses_Exception() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolved(user.getId())).thenThrow(new RuntimeException("Erro inesperado"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseService.getExpenses(token);
        });

        assertTrue(exception.getMessage().contains("Erro ao listar despesa."));
    }

    @Test
    @DisplayName("Deve retornar a lista de despesas pendentes do usuário com sucesso")
    public void testGetExpensesPending_Success() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        Expense expense1 = new Expense();
        expense1.setStatus(Status.PENDING);
        expense1.setDate(LocalDate.of(2024, 8, 20));

        Expense expense2 = new Expense();
        expense2.setStatus(Status.PENDING);
        expense2.setDate(LocalDate.of(2024, 8, 22));

        List<Expense> expenseList = new ArrayList<>();
        expenseList.add(expense1);
        expenseList.add(expense2);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PENDING)).thenReturn(expenseList);

        List<ExpenseDto> result = expenseService.getExpensesPending(token);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(expense1.getStatus(), result.get(0).status());
        assertEquals(expense2.getStatus(), result.get(1).status());
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando não houver despesas pendentes")
    public void testGetExpensesPending_ExpenseNotFound() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PENDING)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.getExpensesPending(token);
        });

        assertTrue(exception.getMessage().contains("Não há despesas pendentes no momento."));
    }

    @Test
    @DisplayName("Deve retornar a lista de despesas pagas do usuário com sucesso")
    public void testGetExpensesPaid_Success() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        Expense expense1 = new Expense();
        expense1.setStatus(Status.PAID);
        expense1.setDate(LocalDate.of(2024, 8, 20));

        Expense expense2 = new Expense();
        expense2.setStatus(Status.PAID);
        expense2.setDate(LocalDate.of(2024, 8, 22));

        List<Expense> expenseList = new ArrayList<>();
        expenseList.add(expense1);
        expenseList.add(expense2);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PAID)).thenReturn(expenseList);

        List<ExpenseDto> result = expenseService.getExpensesPaid(token);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(expense1.getStatus(), result.get(0).status());
        assertEquals(expense2.getStatus(), result.get(1).status());
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando não houver despesas pagas")
    public void testGetExpensesPaid_ExpenseNotFound() {

        String token = "valid-token";
        User user = new User();
        user.setId(1L);

        when(tokenService.getUserFromToken(token)).thenReturn(user);
        when(expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PAID)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ExpenseNotFoundException.class, () -> {
            expenseService.getExpensesPaid(token);
        });

        assertTrue(exception.getMessage().contains("Não há despesas pagas no momento."));
    }
}