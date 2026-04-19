package org.example;

public class Calculator {

    // Basic arithmetic operations

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return a / b;
    }

    // Fibonacci numbers
    // F(0) = 0, F(1) = 1, F(n) = F(n-1) + F(n-2)

    public long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, but was: " + n);
        }
        if (n == 0) return 0L;
        if (n == 1) return 1L;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // Lucas numbers
    // L(0) = 2, L(1) = 1, L(n) = L(n-1) + L(n-2)

    public long lucas(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, but was: " + n);
        }
        if (n == 0) return 2L;
        if (n == 1) return 1L;
        return lucas(n - 1) + lucas(n - 2);
    }
}
