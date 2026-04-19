package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * All possible test cases for Calculator.fibonacci() using JUnit 5 + AssertJ.
 *
 * Sequence: F(0)=0, F(1)=1, F(2)=1, F(3)=2, F(4)=3, F(5)=5,
 *           F(6)=8, F(7)=13, F(8)=21, F(9)=34, F(10)=55 …
 */
@DisplayName("Calculator – fibonacci()")
class FibonacciTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // Boundary / base cases

    @Test
    @DisplayName("F(0) = 0  [lower boundary]")
    void fibonacci_zero_returnsZero() {
        assertThat(calculator.fibonacci(0)).isEqualTo(0L);
    }

    @Test
    @DisplayName("F(1) = 1  [first base case]")
    void fibonacci_one_returnsOne() {
        assertThat(calculator.fibonacci(1)).isEqualTo(1L);
    }

    @Test
    @DisplayName("F(2) = 1  [first recursive result]")
    void fibonacci_two_returnsOne() {
        assertThat(calculator.fibonacci(2)).isEqualTo(1L);
    }

    // Small valid values

    @Test
    @DisplayName("F(3) = 2")
    void fibonacci_three_returnsTwo() {
        assertThat(calculator.fibonacci(3)).isEqualTo(2L);
    }

    @Test
    @DisplayName("F(4) = 3")
    void fibonacci_four_returnsThree() {
        assertThat(calculator.fibonacci(4)).isEqualTo(3L);
    }

    @Test
    @DisplayName("F(5) = 5")
    void fibonacci_five_returnsFive() {
        assertThat(calculator.fibonacci(5)).isEqualTo(5L);
    }

    @Test
    @DisplayName("F(6) = 8")
    void fibonacci_six_returnsEight() {
        assertThat(calculator.fibonacci(6)).isEqualTo(8L);
    }

    @Test
    @DisplayName("F(7) = 13")
    void fibonacci_seven_returnsThirteen() {
        assertThat(calculator.fibonacci(7)).isEqualTo(13L);
    }

    // Larger valid values

    @Test
    @DisplayName("F(10) = 55")
    void fibonacci_ten_returnsFiftyFive() {
        assertThat(calculator.fibonacci(10)).isEqualTo(55L);
    }

    @Test
    @DisplayName("F(15) = 610")
    void fibonacci_fifteen_returns610() {
        assertThat(calculator.fibonacci(15)).isEqualTo(610L);
    }

    @Test
    @DisplayName("F(20) = 6765")
    void fibonacci_twenty_returns6765() {
        assertThat(calculator.fibonacci(20)).isEqualTo(6765L);
    }

    // Result is non-negative for all valid inputs

    @Test
    @DisplayName("fibonacci() result is always >= 0 for n >= 0")
    void fibonacci_validInput_resultIsNonNegative() {
        assertThat(calculator.fibonacci(0)).isGreaterThanOrEqualTo(0L);
        assertThat(calculator.fibonacci(10)).isGreaterThanOrEqualTo(0L);
        assertThat(calculator.fibonacci(20)).isGreaterThanOrEqualTo(0L);
    }

    // Recurrence relation holds

    @Test
    @DisplayName("F(n) = F(n-1) + F(n-2) for n > 1")
    void fibonacci_satisfiesRecurrenceRelation() {
        for (int n = 2; n <= 10; n++) {
            assertThat(calculator.fibonacci(n))
                    .as("F(%d) should equal F(%d) + F(%d)", n, n - 1, n - 2)
                    .isEqualTo(calculator.fibonacci(n - 1) + calculator.fibonacci(n - 2));
        }
    }

    // Negative (invalid) partition

    @Test
    @DisplayName("Negative: F(-1) throws IllegalArgumentException")
    void fibonacci_minusOne_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> calculator.fibonacci(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    @DisplayName("Negative: F(-5) throws IllegalArgumentException")
    void fibonacci_negativeFive_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> calculator.fibonacci(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-5");
    }

    @Test
    @DisplayName("Negative: exception message mentions the invalid value")
    void fibonacci_negativeInput_exceptionMessageContainsValue() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.fibonacci(-10))
                .withMessageContaining("-10");
    }
}
