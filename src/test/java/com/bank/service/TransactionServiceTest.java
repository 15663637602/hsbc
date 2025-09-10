package com.bank.service;

import com.bank.exception.TransactionAlreadyExistsException;
import com.bank.exception.TransactionNotFoundException;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.TransactionRepository;
import com.bank.service.impl.TransactionServiceImpl;
import com.google.common.cache.Cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;

    @Mock
    private Cache<String, Object> transactionByReferenceCache;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setReference("TEST123");
        transaction.setAccountNumber("LYQ001");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription("Test deposit");
    }

    @Test
    void createTransaction_ShouldSaveAndReturnTransaction() {
        when(transactionRepository.findByReference(transaction.getReference())).thenReturn(Optional.empty());
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction created = transactionService.createTransaction(transaction);

        assertNotNull(created);
        assertEquals(transaction.getReference(), created.getReference());
        verify(transactionRepository).findByReference(transaction.getReference());
        verify(transactionByReferenceCache, times(1)).put(eq(transaction.getReference()), eq(transaction));
        verify(transactionRepository).save(transaction);
    }

    @Test
    void createTransaction_WithExistingReference_ShouldThrowException() {
        when(transactionRepository.findByReference(transaction.getReference())).thenReturn(Optional.of(transaction));

        assertThrows(TransactionAlreadyExistsException.class, () -> {
            transactionService.createTransaction(transaction);
        });
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getAllTransactions_ShouldReturnPageOfTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction, new Transaction(2L, "REF124", "LYQ001",
                new BigDecimal("200.00"), TransactionType.WITHDRAWAL, "Test withdrawal"));
        Page<Transaction> transactionPage = new PageImpl<>(transactions);
        
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(transactionPage);

        Page<Transaction> result = transactionService.getAllTransactions(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void updateTransaction_ExistingId_ShouldUpdateAndReturnTransaction() {
        Transaction updatedDetails = new Transaction();
        updatedDetails.setReference("TEST123");
        updatedDetails.setAccountNumber("LYQ001");
        updatedDetails.setAmount(new BigDecimal("150.00"));
        updatedDetails.setType(TransactionType.DEPOSIT);
        updatedDetails.setDescription("Updated deposit");

        when(transactionRepository.findByReference(updatedDetails.getReference())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction updated = transactionService.updateTransaction("TEST123", updatedDetails);

        assertNotNull(updated);
        assertEquals(updatedDetails.getReference(), updated.getReference());
        assertEquals(updatedDetails.getAmount(), updated.getAmount());
        verify(transactionRepository).findByReference(updatedDetails.getReference());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_ExistingId_ShouldDeleteTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).deleteById(1L);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository).findById(1L);
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void deleteTransaction_NonExistingId_ShouldThrowException() {
        when(transactionRepository.findById(99L)).thenThrow(TransactionNotFoundException.class);

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(99L);
        });
        verify(transactionRepository, never()).deleteById(any());
    }
}
