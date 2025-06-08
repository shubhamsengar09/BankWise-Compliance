package com.bankwise.aml_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp=LocalDateTime.now();
    private String counterparty;
    private String transactionType;
    private boolean suspicious;
    private String suspiciousReason;
    private String status;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}