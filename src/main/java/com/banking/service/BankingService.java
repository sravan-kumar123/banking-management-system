package com.banking.service;

import com.banking.dao.AccountDAO;
import com.banking.dao.AccountDAOImpl;
import com.banking.dao.TransactionDAO;
import com.banking.dao.TransactionDAOImpl;
import com.banking.db.DBConnection;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAmountException;
import com.banking.model.Account;
import com.banking.model.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Business/service layer that sits between the presentation layer (Main / CLI)
 * and the DAO layer. Responsible for validation and for coordinating multi-step
 * database operations (e.g. balance update + transaction log entry) inside a
 * single JDBC transaction so they either both succeed or both roll back.
 */
public class BankingService {

    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public BankingService() {
        this.accountDAO = new AccountDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
    }

    // Constructor for dependency injection / testing with mock DAOs
    public BankingService(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    public Account openAccount(String holderName, String accountType, BigDecimal openingBalance,
                                String email, String phone) throws SQLException, InvalidAmountException {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new InvalidAmountException("Account holder name cannot be empty.");
        }
        if (openingBalance == null || openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative.");
        }
        Account account = new Account(holderName, accountType, openingBalance, email, phone);
        return accountDAO.createAccount(account);
    }

    public Account getAccount(long accountId) throws SQLException, AccountNotFoundException {
        return accountDAO.getAccountById(accountId);
    }

    public List<Account> getAllAccounts() throws SQLException {
        return accountDAO.getAllAccounts();
    }

    public BigDecimal checkBalance(long accountId) throws SQLException, AccountNotFoundException {
        return accountDAO.getAccountById(accountId).getBalance();
    }

    /**
     * Deposits an amount into the given account. Balance update and transaction
     * log insert are performed atomically.
     */
    public Account deposit(long accountId, BigDecimal amount) throws SQLException, AccountNotFoundException, InvalidAmountException {
        validateAmount(amount);
        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Account account = accountDAO.getAccountById(accountId);
            BigDecimal newBalance = account.getBalance().add(amount);

            accountDAO.updateBalance(conn, accountId, newBalance);
            transactionDAO.addTransaction(conn,
                    new Transaction(accountId, "DEPOSIT", amount, newBalance, "Cash deposit"));

            conn.commit();
            account.setBalance(newBalance);
            return account;
        } catch (SQLException | AccountNotFoundException e) {
            safeRollback(conn);
            throw e;
        } finally {
            restoreAutoCommit(conn, originalAutoCommit);
        }
    }

    /**
     * Withdraws an amount from the given account, rejecting the operation if
     * funds are insufficient. Balance update and transaction log insert are
     * performed atomically.
     */
    public Account withdraw(long accountId, BigDecimal amount)
            throws SQLException, AccountNotFoundException, InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Account account = accountDAO.getAccountById(accountId);
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient funds. Available balance: " + account.getBalance());
            }
            BigDecimal newBalance = account.getBalance().subtract(amount);

            accountDAO.updateBalance(conn, accountId, newBalance);
            transactionDAO.addTransaction(conn,
                    new Transaction(accountId, "WITHDRAWAL", amount, newBalance, "Cash withdrawal"));

            conn.commit();
            account.setBalance(newBalance);
            return account;
        } catch (SQLException | AccountNotFoundException | InsufficientFundsException e) {
            safeRollback(conn);
            throw e;
        } finally {
            restoreAutoCommit(conn, originalAutoCommit);
        }
    }

    /**
     * Transfers funds between two accounts as a single atomic transaction:
     * a debit from the source account and a credit to the destination account
     * either both commit or both roll back.
     */
    public void transfer(long fromAccountId, long toAccountId, BigDecimal amount)
            throws SQLException, AccountNotFoundException, InsufficientFundsException, InvalidAmountException {
        validateAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new InvalidAmountException("Source and destination accounts must be different.");
        }

        Connection conn = DBConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Account from = accountDAO.getAccountById(fromAccountId);
            Account to = accountDAO.getAccountById(toAccountId);

            if (from.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient funds in source account. Available balance: " + from.getBalance());
            }

            BigDecimal fromNewBalance = from.getBalance().subtract(amount);
            BigDecimal toNewBalance = to.getBalance().add(amount);

            accountDAO.updateBalance(conn, fromAccountId, fromNewBalance);
            accountDAO.updateBalance(conn, toAccountId, toNewBalance);

            transactionDAO.addTransaction(conn,
                    new Transaction(fromAccountId, "TRANSFER_OUT", amount, fromNewBalance,
                            "Transfer to account " + toAccountId));
            transactionDAO.addTransaction(conn,
                    new Transaction(toAccountId, "TRANSFER_IN", amount, toNewBalance,
                            "Transfer from account " + fromAccountId));

            conn.commit();
        } catch (SQLException | AccountNotFoundException | InsufficientFundsException e) {
            safeRollback(conn);
            throw e;
        } finally {
            restoreAutoCommit(conn, originalAutoCommit);
        }
    }

    public List<Transaction> getTransactionHistory(long accountId) throws SQLException, AccountNotFoundException {
        // Validate the account exists before pulling its history
        accountDAO.getAccountById(accountId);
        return transactionDAO.getTransactionsByAccountId(accountId);
    }

    public boolean closeAccount(long accountId) throws SQLException {
        return accountDAO.deleteAccount(accountId);
    }

    private void validateAmount(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
    }

    private void safeRollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException rollbackEx) {
            System.err.println("Rollback failed: " + rollbackEx.getMessage());
        }
    }

    private void restoreAutoCommit(Connection conn, boolean originalAutoCommit) {
        try {
            if (conn != null) {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("Failed to restore auto-commit state: " + e.getMessage());
        }
    }
}
