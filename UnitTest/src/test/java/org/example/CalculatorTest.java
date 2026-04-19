package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Calculator.divide() using JUnit 5.
 * Covers positive (valid) and negative (invalid) equivalence partitions.
 */
@DisplayName("Calculator – divide()")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // Positive partition (valid inputs)

    @Test
    @DisplayName("Positive: 10 / 2 = 5")
    void divide_positiveByPositive_returnsInt() {
        assertEquals(5, calculator.divide(10, 2));
    }

    @Test
    @DisplayName("Positive: 0 / 5 = 0")
    void divide_zeroByPositive_returnsZero() {
        assertEquals(0, calculator.divide(0, 5));
    }

    @Test
    @DisplayName("Positive: -10 / -2 = 5  (both negative → positive)")
    void divide_negativeByNegative_returnsPositiveInt() {
        assertEquals(5, calculator.divide(-10, -2));
    }

    @Test
    @DisplayName("Positive: -10 / 2 = -5  (mixed signs → negative)")
    void divide_negativeByPositive_returnsNegativeInt() {
        assertEquals(-5, calculator.divide(-10, 2));
    }

    @Test
    @DisplayName("Positive: 7 / 2 = 3  (non-integer result)")
    void divide_oddDividend_returnsWholePart() {
        assertEquals(3, calculator.divide(7, 2));
    }

    // Negative partition (invalid inputs)

    @Test
    @DisplayName("Negative: division by zero throws ArithmeticException")
    void divide_byZero_throwsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(5, 0));
    }

    @Test
    @DisplayName("Negative: 0 / 0 also throws ArithmeticException")
    void divide_zeroByZero_throwsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(0, 0));
    }
}
