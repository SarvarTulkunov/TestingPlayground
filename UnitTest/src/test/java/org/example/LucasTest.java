package org.example;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * All possible test cases for Calculator.lucas() using TestNG.
 *
 * Lucas sequence: L(0)=2, L(1)=1, L(2)=3, L(3)=4, L(4)=7, L(5)=11,
 *                 L(6)=18, L(7)=29, L(8)=47, L(9)=76, L(10)=123 …
 *
 * Groups:
 *   "valid"   – equivalence partition: n >= 0 (expected numeric results)
 *   "invalid" – equivalence partition: n < 0  (expected exceptions)
 *
 * Priorities run in ascending order (lower number = runs first).
 */
public class LucasTest {

    private Calculator calculator;

    @BeforeMethod
    public void setUp() {
        calculator = new Calculator();
    }

    // Valid partition – base cases

    @Test(priority = 1, groups = {"valid"},
            description = "L(0) = 2 – lower boundary / first base case")
    public void lucas_zero_returnsTwo() {
        assertEquals(2L, calculator.lucas(0));
    }

    @Test(priority = 2, groups = {"valid"},
            description = "L(1) = 1 – second base case")
    public void lucas_one_returnsOne() {
        assertEquals(1L, calculator.lucas(1));
    }

    @Test(priority = 3, groups = {"valid"},
            description = "L(2) = 3 – first recursive result")
    public void lucas_two_returnsThree() {
        assertEquals(3L, calculator.lucas(2));
    }

    // Valid partition – small values

    @Test(priority = 4, groups = {"valid"},
            description = "L(3) = 4")
    public void lucas_three_returnsFour() {
        assertEquals(4L, calculator.lucas(3));
    }

    @Test(priority = 5, groups = {"valid"},
            description = "L(4) = 7")
    public void lucas_four_returnsSeven() {
        assertEquals(7L, calculator.lucas(4));
    }

    @Test(priority = 6, groups = {"valid"},
            description = "L(5) = 11")
    public void lucas_five_returnsEleven() {
        assertEquals(11L, calculator.lucas(5));
    }

    @Test(priority = 7, groups = {"valid"},
            description = "L(6) = 18")
    public void lucas_six_returnsEighteen() {
        assertEquals(18L, calculator.lucas(6));
    }

    @Test(priority = 8, groups = {"valid"},
            description = "L(7) = 29")
    public void lucas_seven_returnsTwentyNine() {
        assertEquals(29L, calculator.lucas(7));
    }

    // Valid partition – larger values

    @Test(priority = 9, groups = {"valid"},
            description = "L(10) = 123")
    public void lucas_ten_returns123() {
        assertEquals(123L, calculator.lucas(10));
    }

    @Test(priority = 10, groups = {"valid"},
            description = "L(15) = 1364")
    public void lucas_fifteen_returns1364() {
        assertEquals(1364L, calculator.lucas(15));
    }

    @Test(priority = 11, groups = {"valid"},
            description = "Result is always positive for n >= 0")
    public void lucas_validInputs_resultsArePositive() {
        assertTrue(calculator.lucas(0) > 0);
        assertTrue(calculator.lucas(5) > 0);
        assertTrue(calculator.lucas(10) > 0);
    }

    @Test(priority = 12, groups = {"valid"},
            description = "Recurrence: L(n) = L(n-1) + L(n-2) for n > 1")
    public void lucas_satisfiesRecurrenceRelation() {
        for (int n = 2; n <= 10; n++) {
            assertEquals(
                    calculator.lucas(n - 1) + calculator.lucas(n - 2),
                    calculator.lucas(n),
                    "L(" + n + ") should equal L(" + (n - 1) + ") + L(" + (n - 2) + ")"
            );
        }
    }

    // Invalid partition – negative inputs

    @Test(priority = 13, groups = {"invalid"},
            description = "L(-1) must throw IllegalArgumentException",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*-1.*")
    public void lucas_minusOne_throwsIllegalArgumentException() {
        calculator.lucas(-1);
    }

    @Test(priority = 14, groups = {"invalid"},
            description = "L(-5) must throw IllegalArgumentException",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*-5.*")
    public void lucas_negativeFive_throwsIllegalArgumentException() {
        calculator.lucas(-5);
    }

    @Test(priority = 15, groups = {"invalid"},
            description = "L(-100) must throw IllegalArgumentException",
            expectedExceptions = IllegalArgumentException.class)
    public void lucas_largeNegative_throwsIllegalArgumentException() {
        calculator.lucas(-100);
    }

    @Test(priority = 16, groups = {"invalid"},
            description = "Exception message must mention the invalid value")
    public void lucas_negativeInput_exceptionMessageContainsValue() {
        try {
            calculator.lucas(-7);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("-7"),
                    "Message should contain the invalid value -7, but was: " + e.getMessage());
        }
    }
}
