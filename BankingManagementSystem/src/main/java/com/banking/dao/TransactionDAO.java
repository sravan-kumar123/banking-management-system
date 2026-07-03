package com.banking.dao;

import com.banking.model.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object contract for {@link Transaction} persistence operations.
 */
public interface TransactionDAO {

    /**
     * Inserts a transaction record as part of a caller-managed transaction so it can
     * be committed or rolled back together with the related account balance update.
     */
    Transaction addTransaction(Connection conn, Transaction transaction) throws SQLException;

    List<Transaction> getTransactionsByAccountId(long accountId) throws SQLException;
}
