package com.application.eurder_project.service.validator;

/**
 * Small, stateless checks shared by the request validators.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isNotFilledIn(String string) {
        return string == null || string.isBlank();
    }

    public static boolean isNotFilledIn(Object value) {
        return value == null || (value instanceof Double number && number.isNaN());
    }

    /** True for numbers below 0. A missing number (null) is not negative. */
    public static boolean isNegative(Number number) {
        return number != null && number.doubleValue() < 0;
    }

    /** True for numbers of 0 or below. A missing number (null) is not "not positive". */
    public static boolean isNotPositive(Number number) {
        return number != null && number.doubleValue() <= 0;
    }
}
