package com.bankwise.aml_backend.controller;

import com.bankwise.aml_backend.model.Transaction;
import com.bankwise.aml_backend.service.AmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final AmlService amlService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(amlService.getAllTransactions());
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction analyzed = amlService.analyzeTransaction(transaction);
        return ResponseEntity.ok(analyzed);
    }

    @GetMapping("/suspicious")
    public ResponseEntity<List<Transaction>> getSuspiciousTransactions() {
        return ResponseEntity.ok(amlService.getSuspiciousTransactions());
    }

    @GetMapping("/realtime")
    public SseEmitter realtimeMonitoring() {
        SseEmitter emitter = new SseEmitter();
        // Simulate real-time updates (we'll improve this later)
        new Thread(() -> {
            try {
                emitter.send("Real-time AML updates enabled");
                Thread.sleep(1000);
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
}
