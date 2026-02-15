package com._4meonweb.probability;

import java.util.stream.IntStream;

/**
 * Provides combinatorics utility functions compatible with R's factorial,
 * choose, lfactorial, and lchoose.
 * All methods use modern Java approaches with proper validation and edge case
 * handling.
 */
public class Combinatorics {

    private Combinatorics() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Computes the factorial of n (n!).
     * Equivalent to R's factorial(n).
     *
     * @param n the non-negative integer
     * @return n! as a long value
     * @throws IllegalArgumentException if n < 0
     * @throws ArithmeticException      if n > 20 (overflow)
     */
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got: " + n);
        }
        if (n > 20) {
            throw new ArithmeticException("factorial overflow: n must be <= 20, got: " + n);
        }

        return IntStream.rangeClosed(1, n)
                .mapToLong(i -> i)
                .reduce(1L, (a, b) -> a * b);
    }

    /**
     * Computes the binomial coefficient "n choose k" using the multiplicative
     * formula.
     * Equivalent to R's choose(n, k).
     *
     * @param n the total number of items
     * @param k the number of items to choose
     * @return the binomial coefficient C(n, k)
     * @throws IllegalArgumentException if k < 0
     */
    public static long choose(int n, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative, got: " + k);
        }
        if (k > n) {
            return 0;
        }
        if (k == 0 || k == n) {
            return 1;
        }

        // Optimize by using smaller of k or n-k
        k = Math.min(k, n - k);

        // Use multiplicative formula to avoid overflow as long as possible
        long result = 1;
        for (int i = 0; i < k; i++) {
            result = result * (n - i) / (i + 1);
        }

        return result;
    }

    /**
     * Computes the natural logarithm of the factorial of n (log(n!)).
     * Equivalent to R's lfactorial(n).
     *
     * @param n the non-negative integer
     * @return log(n!) as a double value
     * @throws IllegalArgumentException if n < 0
     */
    public static double logFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got: " + n);
        }

        return IntStream.rangeClosed(2, n)
                .mapToDouble(Math::log)
                .sum();
    }

    /**
     * Computes the natural logarithm of the binomial coefficient "n choose k".
     * Equivalent to R's lchoose(n, k).
     *
     * @param n the total number of items
     * @param k the number of items to choose
     * @return log(C(n, k))
     * @throws IllegalArgumentException if k < 0
     */
    public static double logChoose(int n, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative, got: " + k);
        }
        if (k > n) {
            return Double.NEGATIVE_INFINITY;
        }
        if (k == 0 || k == n) {
            return 0.0;
        }

        return logFactorial(n) - logFactorial(k) - logFactorial(n - k);
    }
}
