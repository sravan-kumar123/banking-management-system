package com.banking.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Small helper for reading and validating console input, keeping Main's
 * menu loop free of repetitive try/catch parsing logic.
 */
public final class InputValidator {

    private InputValidator() {
    }

    public static long readLong(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric account ID.");
            }
        }
    }

    public static BigDecimal readAmount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount (e.g. 1500.00).");
            }
        }
    }

    public static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }
}
