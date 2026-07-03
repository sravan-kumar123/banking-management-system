package com.banking.dao;

import com.banking.db.DBConnection;
import com.banking.exception.AccountNotFoundException;
import com.banking.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of {@link AccountDAO} backed by Oracle Database.
 * Expects the ACCOUNTS table and ACCOUNTS_SEQ sequence created by sql/schema.sql.
 */
public class AccountDAOImpl implements AccountDAO {

    private static final String INSERT_SQL =
            "INSERT INTO accounts (account_id, account_holder_name, account_type, balance, email, phone_number, created_date) " +
                    "VALUES (accounts_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSTIMESTAMP)";

    private static final String SELECT_BY_ID_SQL =
            "SELECT account_id, account_holder_name, account_type, balance, email, phone_number, created_date " +
                    "FROM accounts WHERE account_id = ?";

    private static final String SELECT_ALL_SQL =
            "SELECT account_id, account_holder_name, account_type, balance, email, phone_number, created_date " +
                    "FROM accounts ORDER BY account_id";

    private static final String UPDATE_BALANCE_SQL =
            "UPDATE accounts SET balance = ? WHERE account_id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM accounts WHERE account_id = ?";

    private static final String EXISTS_SQL =
            "SELECT 1 FROM accounts WHERE account_id = ?";

    @Override
    public Account createAccount(Account account) throws SQLException {
        Connection conn = DBConnection.getConnection();
        // RETURN_GENERATED_KEYS lets us pull back the sequence-generated account_id
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, new String[]{"account_id"})) {
            ps.setString(1, account.getAccountHolderName());
            ps.setString(2, account.getAccountType());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getEmail());
            ps.setString(5, account.getPhoneNumber());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setAccountId(keys.getLong(1));
                }
            }
        }
        return account;
    }

    @Override
    public Account getAccountById(long accountId) throws SQLException, AccountNotFoundException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new AccountNotFoundException("No account found with ID: " + accountId);
    }

    @Override
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        }
        return accounts;
    }

    @Override
    public void updateBalance(long accountId, BigDecimal newBalance) throws SQLException {
        updateBalance(DBConnection.getConnection(), accountId, newBalance);
    }

    @Override
    public void updateBalance(Connection conn, long accountId, BigDecimal newBalance) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_BALANCE_SQL)) {
            ps.setBigDecimal(1, newBalance);
            ps.setLong(2, accountId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean deleteAccount(long accountId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setLong(1, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsById(long accountId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setAccountHolderName(rs.getString("account_holder_name"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setEmail(rs.getString("email"));
        account.setPhoneNumber(rs.getString("phone_number"));
        Timestamp ts = rs.getTimestamp("created_date");
        if (ts != null) {
            account.setCreatedDate(ts.toLocalDateTime());
        }
        return account;
    }
}
