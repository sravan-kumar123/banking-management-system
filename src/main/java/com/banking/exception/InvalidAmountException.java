package com.banking.exception;

/**
 * Thrown when a monetary amount supplied to the application is invalid,
 * e.g. zero, negative, or exceeds allowed precision.
 */
public class InvalidAmountException extends Exception {

    public InvalidAmountException(String message) {
        super(message);
    }
}
