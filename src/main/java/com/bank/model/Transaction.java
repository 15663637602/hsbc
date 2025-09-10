package com.bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Reference cannot be blank")
    @Column(unique = true, nullable = false)
    private String reference;

    @NotBlank(message = "Account number cannot be blank")
    @Column(nullable = false, name = "account_number")
    @JsonProperty("account_number")
    private String accountNumber;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must greater than zero")
    private BigDecimal amount;

    @NotNull(message = "type cannot be null")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    @Column(nullable = false, updatable = false, name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public Transaction(Long id, String reference, String accountNumber, BigDecimal amount, TransactionType type, String description) {
        this.id = id;
        this.reference = reference;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    /**
     * 首次保存到数据库前自动执行
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 仅更新时自动执行
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
