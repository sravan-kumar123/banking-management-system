package com.banking.exception;

/**
 * Thrown when a withdrawal or transfer is attempted with an amount
 * greater than the available account balance.
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
