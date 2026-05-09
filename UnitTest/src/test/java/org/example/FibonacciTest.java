package org.example;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;

@Epic("Unit Tests")
@Feature("Calculator")
@DisplayName("Calculator – fibonacci()")
class FibonacciTest {

    private static final Logger log = LoggerFactory.getLogger(FibonacciTest.class);

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // Boundary / base cases

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Boundary: F(0) = 0 (lower boundary / first base case)")
    @DisplayName("F(0) = 0  [lower boundary]")
    void fibonacci_zero_returnsZero() {
        log.info("Testing F(0) = 0");
        assertThat(calculator.fibonacci(0)).isEqualTo(0L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Boundary: F(1) = 1 (first base case)")
    @DisplayName("F(1) = 1  [first base case]")
    void fibonacci_one_returnsOne() {
        log.info("Testing F(1) = 1");
        assertThat(calculator.fibonacci(1)).isEqualTo(1L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @Description("F(2) = 1 (first recursive result)")
    @DisplayName("F(2) = 1  [first recursive result]")
    void fibonacci_two_returnsOne() {
        log.info("Testing F(2) = 1");
        assertThat(calculator.fibonacci(2)).isEqualTo(1L);
    }

    // Small valid values

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(3) = 2")
    void fibonacci_three_returnsTwo() {
        log.debug("Testing F(3) = 2");
        assertThat(calculator.fibonacci(3)).isEqualTo(2L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(4) = 3")
    void fibonacci_four_returnsThree() {
        assertThat(calculator.fibonacci(4)).isEqualTo(3L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(5) = 5")
    void fibonacci_five_returnsFive() {
        assertThat(calculator.fibonacci(5)).isEqualTo(5L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(6) = 8")
    void fibonacci_six_returnsEight() {
        assertThat(calculator.fibonacci(6)).isEqualTo(8L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(7) = 13")
    void fibonacci_seven_returnsThirteen() {
        assertThat(calculator.fibonacci(7)).isEqualTo(13L);
    }

    // Larger valid values

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(10) = 55")
    void fibonacci_ten_returnsFiftyFive() {
        assertThat(calculator.fibonacci(10)).isEqualTo(55L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(15) = 610")
    void fibonacci_fifteen_returns610() {
        assertThat(calculator.fibonacci(15)).isEqualTo(610L);
    }

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("F(20) = 6765")
    void fibonacci_twenty_returns6765() {
        assertThat(calculator.fibonacci(20)).isEqualTo(6765L);
    }

    // Result is non-negative for all valid inputs

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.MINOR)
    @Description("All Fibonacci results for n >= 0 must be non-negative")
    @DisplayName("fibonacci() result is always >= 0 for n >= 0")
    void fibonacci_validInput_resultIsNonNegative() {
        log.info("Verifying Fibonacci results are non-negative for n=0,10,20");
        assertThat(calculator.fibonacci(0)).isGreaterThanOrEqualTo(0L);
        assertThat(calculator.fibonacci(10)).isGreaterThanOrEqualTo(0L);
        assertThat(calculator.fibonacci(20)).isGreaterThanOrEqualTo(0L);
    }

    // Recurrence relation holds

    @Test
    @Story("Fibonacci sequence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies F(n) = F(n-1) + F(n-2) for n in [2..10]")
    @DisplayName("F(n) = F(n-1) + F(n-2) for n > 1")
    void fibonacci_satisfiesRecurrenceRelation() {
        log.info("Verifying Fibonacci recurrence relation for n=2..10");
        for (int n = 2; n <= 10; n++) {
            assertThat(calculator.fibonacci(n))
                    .as("F(%d) should equal F(%d) + F(%d)", n, n - 1, n - 2)
                    .isEqualTo(calculator.fibonacci(n - 1) + calculator.fibonacci(n - 2));
        }
    }

    // Negative (invalid) partition

    @Test
    @Story("Invalid input")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Negative partition: F(-1) must throw IllegalArgumentException mentioning the value")
    @DisplayName("Negative: F(-1) throws IllegalArgumentException")
    void fibonacci_minusOne_throwsIllegalArgumentException() {
        log.info("Testing F(-1) throws IllegalArgumentException");
        assertThatThrownBy(() -> calculator.fibonacci(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    @Story("Invalid input")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Negative partition: F(-5) must throw IllegalArgumentException mentioning the value")
    @DisplayName("Negative: F(-5) throws IllegalArgumentException")
    void fibonacci_negativeFive_throwsIllegalArgumentException() {
        log.info("Testing F(-5) throws IllegalArgumentException");
        assertThatThrownBy(() -> calculator.fibonacci(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-5");
    }

    @Test
    @Story("Invalid input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Negative partition: exception message must include the invalid input value")
    @DisplayName("Negative: exception message mentions the invalid value")
    void fibonacci_negativeInput_exceptionMessageContainsValue() {
        log.info("Testing F(-10) exception message contains -10");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.fibonacci(-10))
                .withMessageContaining("-10");
    }
}
