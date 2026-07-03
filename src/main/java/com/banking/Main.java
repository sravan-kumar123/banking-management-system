package com.banking;

import com.banking.db.DBConnection;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAmountException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.service.BankingService;
import com.banking.util.InputValidator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Banking Management System.
 * Presents a simple text menu backed by {@link BankingService}.
 */
public class Main {

    private static final BankingService bankingService = new BankingService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   BANKING MANAGEMENT SYSTEM (JDBC/Oracle)");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createAccount();
                    case "2" -> deposit();
                    case "3" -> withdraw();
                    case "4" -> transfer();
                    case "5" -> checkBalance();
                    case "6" -> transactionHistory();
                    case "7" -> listAllAccounts();
                    case "8" -> closeAccount();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (AccountNotFoundException | InsufficientFundsException | InvalidAmountException e) {
                // Expected business-rule violations - show a friendly message, keep the app running
                System.out.println("Error: " + e.getMessage());
            } catch (SQLException e) {
                // Unexpected database issue - log detail, keep the app running
                System.out.println("A database error occurred. Please try again later.");
                System.err.println("SQLException: " + e.getMessage());
            }
        }

        DBConnection.closeConnection();
        System.out.println("Thank you for using the Banking Management System. Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Check Balance");
        System.out.println("6. Transaction History");
        System.out.println("7. List All Accounts");
        System.out.println("8. Close Account");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void createAccount() throws SQLException, InvalidAmountException {
        String name = InputValidator.readNonEmpty(scanner, "Account Holder Name: ");
        System.out.print("Account Type (SAVINGS/CURRENT): ");
        String type = scanner.nextLine().trim().toUpperCase();
        BigDecimal openingBalance = InputValidator.readAmount(scanner, "Opening Balance: ");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine().trim();

        Account account = bankingService.openAccount(name, type, openingBalance, email, phone);
        System.out.println("Account created successfully! Account ID: " + account.getAccountId());
    }

    private static void deposit() throws SQLException, AccountNotFoundException, InvalidAmountException {
        long accountId = InputValidator.readLong(scanner, "Account ID: ");
        BigDecimal amount = InputValidator.readAmount(scanner, "Deposit Amount: ");
        Account account = bankingService.deposit(accountId, amount);
        System.out.println("Deposit successful. New balance: " + account.getBalance());
    }

    private static void withdraw() throws SQLException, AccountNotFoundException, InsufficientFundsException, InvalidAmountException {
        long accountId = InputValidator.readLong(scanner, "Account ID: ");
        BigDecimal amount = InputValidator.readAmount(scanner, "Withdrawal Amount: ");
        Account account = bankingService.withdraw(accountId, amount);
        System.out.println("Withdrawal successful. New balance: " + account.getBalance());
    }

    private static void transfer() throws SQLException, AccountNotFoundException, InsufficientFundsException, InvalidAmountException {
        long fromId = InputValidator.readLong(scanner, "From Account ID: ");
        long toId = InputValidator.readLong(scanner, "To Account ID: ");
        BigDecimal amount = InputValidator.readAmount(scanner, "Transfer Amount: ");
        bankingService.transfer(fromId, toId, amount);
        System.out.println("Transfer successful.");
    }

    private static void checkBalance() throws SQLException, AccountNotFoundException {
        long accountId = InputValidator.readLong(scanner, "Account ID: ");
        BigDecimal balance = bankingService.checkBalance(accountId);
        System.out.println("Current Balance: " + balance);
    }

    private static void transactionHistory() throws SQLException, AccountNotFoundException {
        long accountId = InputValidator.readLong(scanner, "Account ID: ");
        List<Transaction> history = bankingService.getTransactionHistory(accountId);
        if (history.isEmpty()) {
            System.out.println("No transactions found for this account.");
            return;
        }
        System.out.println("---------------------------------------------------------------");
        for (Transaction t : history) {
            System.out.printf("[%s] %-14s Amount: %-10s Balance After: %-10s %s%n",
                    t.getTransactionDate(), t.getTransactionType(), t.getAmount(),
                    t.getBalanceAfter(), t.getRemarks());
        }
        System.out.println("---------------------------------------------------------------");
    }

    private static void listAllAccounts() throws SQLException {
        List<Account> accounts = bankingService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("---------------------------------------------------------------");
        for (Account a : accounts) {
            System.out.printf("ID: %-6d Name: %-20s Type: %-10s Balance: %s%n",
                    a.getAccountId(), a.getAccountHolderName(), a.getAccountType(), a.getBalance());
        }
        System.out.println("---------------------------------------------------------------");
    }

    private static void closeAccount() throws SQLException {
        long accountId = InputValidator.readLong(scanner, "Account ID to close: ");
        boolean deleted = bankingService.closeAccount(accountId);
        System.out.println(deleted ? "Account closed successfully." : "Account not found.");
    }
}
