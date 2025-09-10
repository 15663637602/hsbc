package com.bank.service.impl;

import com.bank.exception.TransactionAlreadyExistsException;
import com.bank.exception.TransactionNotFoundException;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;
import com.google.common.cache.Cache;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final Cache<String, Object> transactionByReferenceCache;

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        // Check if transaction already exists
        transactionRepository.findByReference(transaction.getReference())
            .ifPresent(existingTransaction -> {
                throw new TransactionAlreadyExistsException(
                    "Transaction with reference " + transaction.getReference() + " already exists");
            });

        Transaction savedTransaction = transactionRepository.save(transaction);

        transactionByReferenceCache.put(savedTransaction.getReference(), savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction getTransactionByReference(String reference) {
        Transaction cachedData = (Transaction) transactionByReferenceCache.getIfPresent(reference);
        if (cachedData != null) {
            return cachedData;
        }

        Transaction transaction = transactionRepository.findByReference(reference)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction with reference " + reference + " not found"));
        transactionByReferenceCache.put(reference, transaction);
        return transaction;
    }

    @Override
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(String reference, Transaction transactionDetails) {
        Transaction existingTransaction = transactionRepository.findByReference(reference)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction with reference: " + reference + " not found"));

        existingTransaction.setAccountNumber(transactionDetails.getAccountNumber());
        existingTransaction.setAmount(transactionDetails.getAmount());
        existingTransaction.setType(transactionDetails.getType());
        existingTransaction.setDescription(transactionDetails.getDescription());

        transactionByReferenceCache.invalidate(reference);

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        transactionByReferenceCache.invalidate(reference);
        return savedTransaction;
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
        transactionRepository.deleteById(id);
        transactionByReferenceCache.invalidate(transaction.getReference());
    }
}
