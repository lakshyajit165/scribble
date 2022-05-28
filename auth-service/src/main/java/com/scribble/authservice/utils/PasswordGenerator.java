package com.scribble.authservice.utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.scribble.authservice.constants.PasswordGeneratorConstants.*;

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    public String generateSecurePassword() {
        StringBuilder result = new StringBuilder(PASSWORD_LENGTH);
        // at least 1 char (lowercase)
        String strLowerCase = generateRandomString(CHAR_LOWERCASE, 1);
        result.append(strLowerCase);
        // at least 1 char (uppercase)
        String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, 1);
        result.append(strUppercaseCase);
        // at least 1 digit
        String strDigit = generateRandomString(DIGIT, 1);
        result.append(strDigit);
        // at least 1 special characters (punctuation + symbols)
        String strSpecialChar = generateRandomString(OTHER_SPECIAL, PASSWORD_LENGTH - result.length());
        result.append(strSpecialChar);
        String password = result.toString();
        // shuffle again
        return shuffleString(password);
    }

    private static String generateRandomString(String input, int size) {
        if (input == null || input.length() <= 0)
            throw new IllegalArgumentException("Invalid input.");
        if (size < 1) throw new IllegalArgumentException("Invalid size.");
        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }

    // for final password, make it more random
    public static String shuffleString(String input) {
        List<String> result = Arrays.asList(input.split(""));
        Collections.shuffle(result);
        return String.join("", result);
    }

}
