package com.banking.dao;

import com.banking.db.DBConnection;
import com.banking.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of {@link TransactionDAO} backed by Oracle Database.
 * Expects the TRANSACTIONS table and TRANSACTIONS_SEQ sequence created by sql/schema.sql.
 */
public class TransactionDAOImpl implements TransactionDAO {

    private static final String INSERT_SQL =
            "INSERT INTO transactions (transaction_id, account_id, transaction_type, amount, balance_after, remarks, transaction_date) " +
                    "VALUES (transactions_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

    private static final String SELECT_BY_ACCOUNT_SQL =
            "SELECT transaction_id, account_id, transaction_type, amount, balance_after, remarks, transaction_date " +
                    "FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";

    @Override
    public Transaction addTransaction(Connection conn, Transaction transaction) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, new String[]{"transaction_id"})) {
            ps.setLong(1, transaction.getAccountId());
            ps.setString(2, transaction.getTransactionType());
            ps.setBigDecimal(3, transaction.getAmount());
            ps.setBigDecimal(4, transaction.getBalanceAfter());
            ps.setString(5, transaction.getRemarks());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setTransactionId(keys.getLong(1));
                }
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(long accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ACCOUNT_SQL)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRow(rs));
                }
            }
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("transaction_id"));
        t.setAccountId(rs.getLong("account_id"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setBalanceAfter(rs.getBigDecimal("balance_after"));
        t.setRemarks(rs.getString("remarks"));
        Timestamp ts = rs.getTimestamp("transaction_date");
        if (ts != null) {
            t.setTransactionDate(ts.toLocalDateTime());
        }
        return t;
    }
}
