# BankWise-Compliance

BankWise-Compliance is a Spring Boot application that handles banking transaction compliance checks with features like suspicious transaction detection.

---

## Features

- **Transaction Analysis:** Detects suspicious transactions based on:
  - Large transaction amounts (greater than 10,000)
  - High frequency of transactions within a short time window (5 or more in 5 minutes)
  - Rapid movement of funds between deposits and withdrawals
  - Round amount transactions (amounts without cents)
- **Alert Generation:** Automatically generates alerts for suspicious transactions.
- **User Management:** Secure user registration and password management with password encoding.
- **Transaction & Alert Persistence:** Uses repositories for storing transactions, alerts, and user data.
---

## Technologies Used

- Java 17+
- Spring Boot Framework
- Spring Data JPA
- Spring Security (Password Encoding)
- LocalDateTime for timestamp management

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Git
- MySql

### Clone the Repository

```bash
git clone https://github.com/shubhamsengar09/BankWise-Compliance.git
cd BankWise-Compliance
