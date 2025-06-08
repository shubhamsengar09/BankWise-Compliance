package com.bankwise.aml_backend.repository;

import com.bankwise.aml_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySuspiciousTrue();

    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.timestamp > :timestamp")
    List<Transaction> findRecentTransactionsByAccount(
            @Param("accountNumber") String accountNumber,
            @Param("timestamp") LocalDateTime timestamp
    );

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Transaction t " +
            "WHERE t.accountNumber = :accountNumber " +
            "AND t.transactionType = :transactionType " +
            "AND t.amount = :amount " +
            "AND t.timestamp > :timestamp")
    boolean existsByAccountNumberAndTransactionTypeAndAmountAndTimestampAfter(
            @Param("accountNumber") String accountNumber,
            @Param("transactionType") String transactionType,
            @Param("amount") BigDecimal amount,
            @Param("timestamp") LocalDateTime timestamp
    );
}