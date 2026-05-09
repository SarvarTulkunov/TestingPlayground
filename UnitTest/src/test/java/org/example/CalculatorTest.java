package org.example;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Unit Tests")
@Feature("Calculator")
@DisplayName("Calculator – divide()")
class CalculatorTest {

    private static final Logger log = LoggerFactory.getLogger(CalculatorTest.class);

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // Positive partition (valid inputs)

    @Test
    @Story("Division")
    @Severity(SeverityLevel.NORMAL)
    @Description("Positive partition: divides a positive number by a positive number")
    @DisplayName("Positive: 10 / 2 = 5")
    void divide_positiveByPositive_returnsInt() {
        log.info("Testing 10 / 2 = 5");
        assertEquals(5, calculator.divide(10, 2));
    }

    @Test
    @Story("Division")
    @Severity(SeverityLevel.NORMAL)
    @Description("Positive partition: divides zero by a positive number")
    @DisplayName("Positive: 0 / 5 = 0")
    void divide_zeroByPositive_returnsZero() {
        log.info("Testing 0 / 5 = 0");
        assertEquals(0, calculator.divide(0, 5));
    }

    @Test
    @Story("Division")
    @Severity(SeverityLevel.NORMAL)
    @Description("Positive partition: both negative operands yield a positive result")
    @DisplayName("Positive: -10 / -2 = 5  (both negative → positive)")
    void divide_negativeByNegative_returnsPositiveInt() {
        log.info("Testing -10 / -2 = 5");
        assertEquals(5, calculator.divide(-10, -2));
    }

    @Test
    @Story("Division")
    @Severity(SeverityLevel.NORMAL)
    @Description("Positive partition: mixed-sign operands yield a negative result")
    @DisplayName("Positive: -10 / 2 = -5  (mixed signs → negative)")
    void divide_negativeByPositive_returnsNegativeInt() {
        log.info("Testing -10 / 2 = -5");
        assertEquals(-5, calculator.divide(-10, 2));
    }

    @Test
    @Story("Division")
    @Severity(SeverityLevel.MINOR)
    @Description("Positive partition: non-integer result is truncated to whole part")
    @DisplayName("Positive: 7 / 2 = 3  (non-integer result)")
    void divide_oddDividend_returnsWholePart() {
        log.info("Testing 7 / 2 = 3 (integer division)");
        assertEquals(3, calculator.divide(7, 2));
    }

    // Negative partition (invalid inputs)

    @Test
    @Story("Division by zero")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Negative partition: division by zero must throw ArithmeticException")
    @DisplayName("Negative: division by zero throws ArithmeticException")
    void divide_byZero_throwsArithmeticException() {
        log.info("Testing 5 / 0 throws ArithmeticException");
        assertThrows(ArithmeticException.class, () -> calculator.divide(5, 0));
    }

    @Test
    @Story("Division by zero")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Negative partition: 0 / 0 must throw ArithmeticException")
    @DisplayName("Negative: 0 / 0 also throws ArithmeticException")
    void divide_zeroByZero_throwsArithmeticException() {
        log.info("Testing 0 / 0 throws ArithmeticException");
        assertThrows(ArithmeticException.class, () -> calculator.divide(0, 0));
    }
}
