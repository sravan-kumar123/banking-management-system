package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single transaction (deposit / withdrawal / transfer) against an account.
 */
public class Transaction {

    private long transactionId;
    private long accountId;
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String remarks;
    private LocalDateTime transactionDate;

    public Transaction() {
    }

    public Transaction(long accountId, String transactionType, BigDecimal amount,
                        BigDecimal balanceAfter, String remarks) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.remarks = remarks;
    }

    public Transaction(long transactionId, long accountId, String transactionType, BigDecimal amount,
                        BigDecimal balanceAfter, String remarks, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.remarks = remarks;
        this.transactionDate = transactionDate;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", remarks='" + remarks + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
