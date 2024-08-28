package com.mybudget.domain.service;

import com.mybudget.domain.dto.ExpenseDto;
import com.mybudget.domain.dto.UpdateExpenseDto;
import com.mybudget.domain.expense.Expense;
import com.mybudget.domain.expense.Status;
import com.mybudget.domain.user.User;
import com.mybudget.domain.repository.ExpenseRepository;
import com.mybudget.domain.repository.UserRepository;
import com.mybudget.domain.service.validation.ValidateDeleteExpense;
import com.mybudget.domain.service.validation.ValidateNewExpense;
import com.mybudget.infra.exception.ExpenseNotFoundException;
import com.mybudget.infra.exception.ExpenseProcessingException;
import com.mybudget.infra.exception.UnauthorizedAccessException;
import com.mybudget.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidateNewExpense validateNewExpense;

    @Autowired
    private ValidateDeleteExpense validateDeleteExpense;

    @Autowired
    private TokenService tokenService;

    @Transactional
    public ExpenseDto newExpense(String token, ExpenseDto expenseDto) {

        try {

            User user = tokenService.getUserFromToken(token);

            validateNewExpense.validate(expenseDto);

            Expense newExpense = new Expense();
            newExpense.setUser(user);
            newExpense.setAmount(expenseDto.amount());
            newExpense.setDescription(expenseDto.description());
            newExpense.setStatus(Status.PENDING);
            newExpense.setDate(LocalDate.now());

            expenseRepository.save(newExpense);

            return new ExpenseDto(newExpense);

        } catch (ExpenseProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar despesa. ", e);
        }
    }

    @Transactional
    public ExpenseDto updateExpense(String token, UUID expenseId, UpdateExpenseDto updateExpense) {

        try {

            User user = tokenService.getUserFromToken(token);

            Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new ExpenseNotFoundException("Despesa não encontrada"));

            if (!expense.getUser().equals(user)) {
                throw new UnauthorizedAccessException("Você não tem permissão para atualizar esta despesa.");
            }

            if (updateExpense.amount() != null) {
                expense.setAmount(updateExpense.amount());
            }

            if (updateExpense.description() != null) {
                expense.setDescription(updateExpense.description());
            }

            if (updateExpense.status() != null) {
                expense.setStatus(updateExpense.status());
            }

            expenseRepository.save(expense);

            return new ExpenseDto(expense);

        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Erro ao atualizar despesa. " + e.getMessage());
        }
    }

    @Transactional
    public void deleteExpense(String token, UUID expenseId) {

        try {

            User user = tokenService.getUserFromToken(token);

            Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new ExpenseNotFoundException("Despesa não encontrada"));

            validateDeleteExpense.validate(expense, user);

            expenseRepository.delete(expense);

        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar esta despesa.");
        }
    }

    @Transactional
    public List<ExpenseDto> getExpenses(String token) {

        try {

            User user = tokenService.getUserFromToken(token);

            List<Expense> expenseList = expenseRepository.findByUserInvolved(user.getId());
            expenseList.sort(Comparator.comparing(Expense::getDate));

            if (expenseList.isEmpty()) {
                throw new ExpenseNotFoundException("Você não tem despesas registradas no momento.");
            }

            return expenseList.stream().map(ExpenseDto::new).toList();

        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar despesa.");
        }
    }

    @Transactional
    public List<ExpenseDto> getExpensesPending(String token) {

        try {

            User user = tokenService.getUserFromToken(token);

            List<Expense> expensesPending = expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PENDING);
            expensesPending.sort(Comparator.comparing(Expense::getDate));

            if (expensesPending.isEmpty()) {
                throw new ExpenseNotFoundException("Não há despesas pendentes no momento.");
            }

            return expensesPending.stream().map(ExpenseDto::new).toList();

        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ExpenseProcessingException("Erro ao listar despesas pendentes. "+ e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> getExpensesPaid(String token) {

        try {

            User user = tokenService.getUserFromToken(token);

            List<Expense> expensesPaid = expenseRepository.findByUserInvolvedAndStatus(user.getId(), Status.PAID);
            expensesPaid.sort(Comparator.comparing(Expense::getDate));

            if (expensesPaid.isEmpty()) {
                throw new ExpenseNotFoundException("Não há despesas pagas no momento.");
            }

            return expensesPaid.stream().map(ExpenseDto::new).toList();

        } catch (ExpenseNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ExpenseProcessingException("Erro ao listar despesas pagas. "+ e.getMessage());
        }
    }
}