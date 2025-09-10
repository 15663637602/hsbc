package com.bank.controller;

import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction transaction;

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        transaction = new Transaction(1L, "Test123", "LYQ001",
            new BigDecimal("100.00"), TransactionType.DEPOSIT, "Test deposit");

        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);

        ResultActions resultActions = mockMvc.perform(post("/v1/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transaction)));

        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.reference").value("Test123"))
            .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getAllTransactions_ShouldReturnPageOfTransactions() throws Exception {
        List<Transaction> transactions = Arrays.asList(transaction, new Transaction(2L, "TEST124", "LYQ001",
            new BigDecimal("200.00"), TransactionType.WITHDRAWAL, "Test withdrawal"));
        Page<Transaction> transactionPage = new PageImpl<>(transactions);

        when(transactionService.getAllTransactions(any(Pageable.class))).thenReturn(transactionPage);

        mockMvc.perform(get("/v1/transactions")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void updateTransaction_ExistingId_ShouldReturnUpdatedTransaction() throws Exception {
        Transaction updatedTransaction = new Transaction(1L, "Test123-UPDATED", "LYQ001",
            new BigDecimal("150.00"), TransactionType.DEPOSIT, "Updated deposit");

        when(transactionService.updateTransaction(eq("Test123-UPDATED"), any(Transaction.class)))
            .thenReturn(updatedTransaction);

        mockMvc.perform(put("/v1/transactions/reference/Test123-UPDATED")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.reference").value("Test123-UPDATED"))
            .andExpect(jsonPath("$.amount").value(150.00));
    }

    @Test
    void deleteTransaction_ExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(transactionService).deleteTransaction(1L);

        // Act & Assert
        mockMvc.perform(delete("/v1/transactions/id/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction(1L);
    }
}
