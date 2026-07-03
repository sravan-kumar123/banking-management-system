package com.banking.exception;

/**
 * Thrown when an operation references an account ID that does not exist.
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
