package com.bankwise.aml_backend.service;
import java.util.Optional;
import com.bankwise.aml_backend.model.Alert;
import com.bankwise.aml_backend.model.Transaction;
import com.bankwise.aml_backend.model.User;
import com.bankwise.aml_backend.repository.AlertRepository;
import com.bankwise.aml_backend.repository.TransactionRepository;
import com.bankwise.aml_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmlService {
    private static final Logger log = LoggerFactory.getLogger(AmlService.class);
    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Thresholds - could be moved to configuration
    private static final BigDecimal LARGE_AMOUNT_THRESHOLD = new BigDecimal("10000");
    private static final int HIGH_FREQUENCY_COUNT = 5;
    private static final int HIGH_FREQUENCY_MINUTES = 5;
    private static final int RAPID_MOVEMENT_HOURS = 1;

    @Transactional
    public Transaction analyzeTransaction(Transaction transaction) {
        boolean isSuspicious = false;
        StringBuilder reason = new StringBuilder();

        log.info("\n--- NEW TRANSACTION ANALYSIS ---");
        log.info("Incoming transaction: {}", transaction);

        validateTransaction(transaction);

        if (isLargeAmount(transaction)) {
            isSuspicious = true;
            reason.append("Large transaction (>").append(LARGE_AMOUNT_THRESHOLD).append("); ");
            log.info("Triggered: Large amount rule");
        }

        if (isHighFrequency(transaction)) {
            isSuspicious = true;
            reason.append("High frequency transaction pattern; ");
            log.info("Triggered: High frequency rule");
        }

        if (isRapidFundsMovement(transaction)) {
            isSuspicious = true;
            reason.append("Rapid funds movement detected; ");
            log.info("Triggered: Rapid funds movement rule");
        }

        if (isRoundAmount(transaction)) {
            isSuspicious = true;
            reason.append("Suspicious round amount; ");
            log.info("Triggered: Round amount rule");
        }

        transaction.setSuspicious(isSuspicious);
        transaction.setSuspiciousReason(reason.toString());
        transaction.setStatus(isSuspicious ? "FLAGGED" : "CLEARED");

        log.info("Final decision - Suspicious: {}, Reason: {}", isSuspicious, reason);

        if (isSuspicious) {
            Alert alert = Alert.builder()
                    .accountNumber(transaction.getAccountNumber())
                    .reason(reason.toString())
                    .timestamp(LocalDateTime.now())
                    .transaction(transaction)
                    .build();
            alertRepository.save(alert);
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getSuspiciousTransactions() {
        return transactionRepository.findBySuspiciousTrue();
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAccountNumber() == null || transaction.getAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
            log.debug("Set default timestamp for transaction");
        }
    }

    private boolean isLargeAmount(Transaction transaction) {
        return transaction.getAmount().compareTo(LARGE_AMOUNT_THRESHOLD) > 0;
    }

    private boolean isHighFrequency(Transaction transaction) {
        LocalDateTime timeWindow = transaction.getTimestamp().minusMinutes(HIGH_FREQUENCY_MINUTES);
        List<Transaction> recentTransactions = transactionRepository
                .findRecentTransactionsByAccount(
                        transaction.getAccountNumber(),
                        timeWindow
                );

        log.debug("Found {} recent transactions for account {} in last {} minutes",
                recentTransactions.size(), transaction.getAccountNumber(), HIGH_FREQUENCY_MINUTES);

        return recentTransactions.size() >= HIGH_FREQUENCY_COUNT;
    }

    private boolean isRapidFundsMovement(Transaction transaction) {
        if (!"WITHDRAWAL".equalsIgnoreCase(transaction.getTransactionType())) {
            return false;
        }

        LocalDateTime timeWindow = transaction.getTimestamp().minusHours(RAPID_MOVEMENT_HOURS);
        return transactionRepository.existsByAccountNumberAndTransactionTypeAndAmountAndTimestampAfter(
                transaction.getAccountNumber(),
                "DEPOSIT",
                transaction.getAmount(),
                timeWindow
        );
    }

    private boolean isRoundAmount(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        return amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User registerOrUpdateUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(existingUser);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
    }
}
