package com.bank.controller;

import com.bank.model.Transaction;
import com.bank.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions API", description = "API for transactions operations")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    @Operation(summary = "Create a new transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transaction created",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Transaction.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Transaction reference already exists",
            content = @Content)
    })
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get a transaction by reference")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the transaction",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Transaction.class))}),
        @ApiResponse(responseCode = "404", description = "Transaction not found",
            content = @Content)
    })
    public ResponseEntity<Transaction> getTransactionByReference(@PathVariable String reference) {
        Transaction transaction = transactionService.getTransactionByReference(reference);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    @Operation(summary = "Get all transactions with pagination")
    @ApiResponse(responseCode = "200", description = "List of transactions",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = Page.class))})
    public ResponseEntity<Page<Transaction>> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/reference/{reference}")
    @Operation(summary = "Update a transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction updated",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Transaction.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Transaction not found",
            content = @Content),
    })
    public ResponseEntity<Transaction> updateTransaction(
        @PathVariable String reference, @Valid @RequestBody Transaction transactionDetails) {
        Transaction updatedTransaction = transactionService.updateTransaction(reference, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/id/{id}")
    @Operation(summary = "Delete a transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted"),
        @ApiResponse(responseCode = "404", description = "Transaction not found",
            content = @Content)
    })
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
