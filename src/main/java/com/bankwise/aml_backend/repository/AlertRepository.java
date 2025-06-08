package com.bankwise.aml_backend.repository;

import com.bankwise.aml_backend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
