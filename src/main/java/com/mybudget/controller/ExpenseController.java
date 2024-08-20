package com.mybudget.controller;

import com.mybudget.domain.dto.ExpenseDto;
import com.mybudget.domain.dto.UpdateExpenseDto;
import com.mybudget.domain.service.ExpenseService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mybudget/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/register")
    public ResponseEntity<ExpenseDto> registerExpense(@RequestHeader("Authorization") String token,
                                                      @RequestBody ExpenseDto expenseDto) {

        ExpenseDto newExpenses = expenseService.newExpense(token, expenseDto);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(newExpenses);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@RequestHeader("Authorization") String token,
                                                    @PathVariable UUID id,
                                                    @RequestBody UpdateExpenseDto expenseDto) {

        ExpenseDto updatedExpense = expenseService.updateExpense(token, id, expenseDto);
        return ResponseEntity.ok(updatedExpense);
    }

    @GetMapping("/list-all")
    public ResponseEntity<List<ExpenseDto>> getExpenseList(@RequestHeader("Authorization") String token) {

        List<ExpenseDto> expense = expenseService.getExpenses(token);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(expense);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseDto>> getExpensePending(@RequestHeader("Authorization") String token) {

        List<ExpenseDto> expense = expenseService.getExpensesPending(token);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(expense);
    }

    @GetMapping("/paid")
    public ResponseEntity<List<ExpenseDto>> getExpensePaid(@RequestHeader("Authorization") String token) {

        List<ExpenseDto> expense = expenseService.getExpensesPaid(token);
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(expense);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteExpense(@RequestHeader("Authorization") String token,
                                              @PathVariable UUID id) {

        expenseService.deleteExpense(token, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}