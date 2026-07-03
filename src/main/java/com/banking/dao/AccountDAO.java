package com.banking.dao;

import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object contract for {@link Account} persistence operations.
 */
public interface AccountDAO {

    Account createAccount(Account account) throws SQLException;

    Account getAccountById(long accountId) throws SQLException, AccountNotFoundException;

    List<Account> getAllAccounts() throws SQLException;

    void updateBalance(long accountId, BigDecimal newBalance) throws SQLException;

    /**
     * Overload that participates in a caller-managed transaction (same Connection,
     * no implicit commit). Used by the service layer for multi-step operations
     * like transfers where multiple DAO calls must succeed or fail together.
     */
    void updateBalance(Connection conn, long accountId, BigDecimal newBalance) throws SQLException;

    boolean deleteAccount(long accountId) throws SQLException;

    boolean existsById(long accountId) throws SQLException;
}
