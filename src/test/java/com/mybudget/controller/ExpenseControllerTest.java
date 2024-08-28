package com.mybudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mybudget.domain.dto.ExpenseDto;
import com.mybudget.domain.dto.UpdateExpenseDto;
import com.mybudget.domain.expense.Status;
import com.mybudget.domain.service.ExpenseService;
import com.mybudget.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ExpenseController expenseController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }

    @Test
    @DisplayName("Deve registrar uma despesa com sucesso")
    public void testRegisterExpense_Success() throws Exception {

        String token = "valid-token";

        ExpenseDto newExpenseDto = new ExpenseDto(UUID.randomUUID(), "description", BigDecimal.valueOf(100.00), Status.PENDING, LocalDate.now());

        when(expenseService.newExpense(anyString(), any(ExpenseDto.class))).thenReturn(newExpenseDto);

        String jsonContent = objectMapper.writeValueAsString(newExpenseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/mybudget/expense/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve atualizar uma despesa com sucesso")
    public void testUpdateExpense_Success() throws Exception {

        UUID id = UUID.randomUUID();
        String token = "valid-token";

        // - O updateExpenseDto é o DTO usado para atualizar a despesa.
        UpdateExpenseDto updateExpenseDto = new UpdateExpenseDto(BigDecimal.valueOf(150.00),"new description", Status.PENDING);

        // - O updatedExpenseDto é a representação da despesa após a atualização ter sido aplicada com sucesso.
        ExpenseDto updatedExpenseDto = new ExpenseDto(id, "new description", BigDecimal.valueOf(150.00), Status.PAID, LocalDate.now());

        when(expenseService.updateExpense(eq("Bearer " + token), eq(id), any(UpdateExpenseDto.class))).thenReturn(updatedExpenseDto);

        String jsonContent = new ObjectMapper().writeValueAsString(updateExpenseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/mybudget/expense/update/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("new description"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("Deve listar todas as despesas com sucesso")
    public void testGetExpenseList_Success() throws Exception {

        String token = "valid-token";

        List<ExpenseDto> expenseList = Arrays.asList(
                new ExpenseDto(UUID.randomUUID(), "description 1", BigDecimal.valueOf(100.00), Status.PENDING, LocalDate.now()),
                new ExpenseDto(UUID.randomUUID(), "description 2", BigDecimal.valueOf(200.00), Status.PAID, LocalDate.now())
        );

        when(expenseService.getExpenses(eq("Bearer " + token))).thenReturn(expenseList);

        mockMvc.perform(get("/mybudget/expense/list-all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expenseList.size()))
                .andExpect(jsonPath("$[0].description").value(expenseList.get(0).description()))
                .andExpect(jsonPath("$[1].amount").value(expenseList.get(1).amount()))
                .andExpect(jsonPath("$[0].status").value(expenseList.get(0).status().toString()));
    }

    @Test
    @DisplayName("Deve listar todas as despesas pendentes com sucesso")
    public void testGetExpensePending_Success() throws Exception {

        String token = "valid-token";

        ExpenseDto expense1 = new ExpenseDto(UUID.randomUUID(), "description 1", BigDecimal.valueOf(100.00), Status.PENDING, LocalDate.now());
        ExpenseDto expense2 = new ExpenseDto(UUID.randomUUID(), "description 2", BigDecimal.valueOf(200.00), Status.PENDING, LocalDate.now());
        List<ExpenseDto> expenseList = Arrays.asList(expense1, expense2);

        when(expenseService.getExpensesPending(anyString())).thenReturn(expenseList);

        mockMvc.perform(get("/mybudget/expense/pending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expenseList.size()))
                .andExpect(jsonPath("$[0].description").value(expense1.description()))
                .andExpect(jsonPath("$[1].amount").value(expense2.amount()))
                .andExpect(jsonPath("$[0].status").value(expense1.status().toString()));
    }

    @Test
    @DisplayName("Deve listar todas as despesas pagas com sucesso")
    public void testGetExpensePaid_Success() throws Exception {

        String token = "valid-token";

        ExpenseDto expense1 = new ExpenseDto(UUID.randomUUID(), "description 1", BigDecimal.valueOf(100.00), Status.PAID, LocalDate.now());
        ExpenseDto expense2 = new ExpenseDto(UUID.randomUUID(), "description 2", BigDecimal.valueOf(200.00), Status.PAID, LocalDate.now());
        List<ExpenseDto> expenseList = Arrays.asList(expense1, expense2);

        when(expenseService.getExpensesPaid(anyString())).thenReturn(expenseList);

        mockMvc.perform(get("/mybudget/expense/paid")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expenseList.size()))
                .andExpect(jsonPath("$[0].description").value(expense1.description()))
                .andExpect(jsonPath("$[1].amount").value(expense2.amount()))
                .andExpect(jsonPath("$[0].status").value(expense1.status().toString()));
    }

    @Test
    @DisplayName("Deve deletar despesa de acordo com ID passado com sucesso")
    public void testDeleteExpense_Success() throws Exception {

        String token = "valid-token";
        UUID expenseID = UUID.randomUUID();

        mockMvc.perform(delete("/mybudget/expense/delete/{id}", expenseID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense("Bearer " + token, expenseID);
    }

}