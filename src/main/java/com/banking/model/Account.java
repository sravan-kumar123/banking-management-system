package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bank account entity.
 */
public class Account {

    private long accountId;
    private String accountHolderName;
    private String accountType; // SAVINGS, CURRENT
    private BigDecimal balance;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdDate;

    public Account() {
    }

    public Account(String accountHolderName, String accountType, BigDecimal balance,
                   String email, String phoneNumber) {
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Account(long accountId, String accountHolderName, String accountType, BigDecimal balance,
                   String email, String phoneNumber, LocalDateTime createdDate) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", accountHolderName='" + accountHolderName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
