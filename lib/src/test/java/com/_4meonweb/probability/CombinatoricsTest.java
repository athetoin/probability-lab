package com._4meonweb.probability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Combinatorics")
class CombinatoricsTest {

    @Nested
    @DisplayName("factorial")
    class FactorialTests {

        @ParameterizedTest
        @DisplayName("computes correct values for small n")
        @CsvSource({
                "0, 1",
                "1, 1",
                "2, 2",
                "3, 6",
                "4, 24",
                "5, 120",
                "6, 720",
                "10, 3628800",
                "20, 2432902008176640000"
        })
        void shouldComputeCorrectFactorial(int n, long expected) {
            assertEquals(expected, Combinatorics.factorial(n));
        }

        @Test
        @DisplayName("throws exception for negative n")
        void shouldThrowForNegativeN() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Combinatorics.factorial(-1));
            assertEquals("n must be non-negative, got: -1", exception.getMessage());
        }

        @Test
        @DisplayName("throws exception for n > 20 (overflow)")
        void shouldThrowForOverflow() {
            ArithmeticException exception = assertThrows(
                    ArithmeticException.class,
                    () -> Combinatorics.factorial(21));
            assertEquals("factorial overflow: n must be <= 20, got: 21", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("choose")
    class ChooseTests {

        @ParameterizedTest
        @DisplayName("computes correct binomial coefficients")
        @CsvSource({
                "5, 0, 1",
                "5, 1, 5",
                "5, 2, 10",
                "5, 3, 10",
                "5, 4, 5",
                "5, 5, 1",
                "10, 3, 120",
                "20, 10, 184756",
                "52, 5, 2598960"
        })
        void shouldComputeCorrectChoose(int n, int k, long expected) {
            assertEquals(expected, Combinatorics.choose(n, k));
        }

        @Test
        @DisplayName("returns 0 when k > n")
        void shouldReturnZeroWhenKGreaterThanN() {
            assertEquals(0, Combinatorics.choose(5, 10));
        }

        @Test
        @DisplayName("returns 1 when k = 0")
        void shouldReturnOneWhenKIsZero() {
            assertEquals(1, Combinatorics.choose(10, 0));
        }

        @Test
        @DisplayName("returns 1 when k = n")
        void shouldReturnOneWhenKEqualsN() {
            assertEquals(1, Combinatorics.choose(10, 10));
        }

        @Test
        @DisplayName("throws exception for negative k")
        void shouldThrowForNegativeK() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Combinatorics.choose(5, -1));
            assertEquals("k must be non-negative, got: -1", exception.getMessage());
        }

        @Test
        @DisplayName("optimizes by using min(k, n-k)")
        void shouldOptimizeKValue() {
            // C(20, 18) = C(20, 2) = 190
            assertEquals(190, Combinatorics.choose(20, 18));
            assertEquals(190, Combinatorics.choose(20, 2));
        }
    }

    @Nested
    @DisplayName("logFactorial")
    class LogFactorialTests {

        private static final double EPSILON = 1e-10;

        @ParameterizedTest
        @DisplayName("computes correct log factorial values")
        @CsvSource({
                "0, 0.0",
                "1, 0.0",
                "2, 0.6931471805599453",
                "3, 1.791759469228055",
                "5, 4.787491742782046",
                "10, 15.104412573075516"
        })
        void shouldComputeCorrectLogFactorial(int n, double expected) {
            assertEquals(expected, Combinatorics.logFactorial(n), EPSILON);
        }

        @Test
        @DisplayName("works for large n without overflow")
        void shouldHandleLargeN() {
            double result = Combinatorics.logFactorial(100);
            // log(100!) ≈ 363.7394
            assertEquals(363.7393755555635, result, EPSILON);
        }

        @Test
        @DisplayName("matches exp(logFactorial(n)) = factorial(n) for small n")
        void shouldMatchExponentialOfFactorial() {
            for (int n = 0; n <= 20; n++) {
                double logFact = Combinatorics.logFactorial(n);
                long fact = Combinatorics.factorial(n);
                assertEquals(fact, Math.exp(logFact), 100.0);
            }
        }

        @Test
        @DisplayName("throws exception for negative n")
        void shouldThrowForNegativeN() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Combinatorics.logFactorial(-1));
            assertEquals("n must be non-negative, got: -1", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("logChoose")
    class LogChooseTests {

        private static final double EPSILON = 1e-10;

        @ParameterizedTest
        @DisplayName("computes correct log binomial coefficients")
        @CsvSource({
                "5, 0, 0.0",
                "5, 5, 0.0",
                "5, 2, 2.302585092994046",
                "10, 3, 4.787491742782046",
                "20, 10, 12.126791314602455"
        })
        void shouldComputeCorrectLogChoose(int n, int k, double expected) {
            assertEquals(expected, Combinatorics.logChoose(n, k), EPSILON);
        }

        @Test
        @DisplayName("returns negative infinity when k > n")
        void shouldReturnNegativeInfinityWhenKGreaterThanN() {
            assertEquals(Double.NEGATIVE_INFINITY, Combinatorics.logChoose(5, 10));
        }

        @Test
        @DisplayName("returns 0 when k = 0")
        void shouldReturnZeroWhenKIsZero() {
            assertEquals(0.0, Combinatorics.logChoose(10, 0));
        }

        @Test
        @DisplayName("returns 0 when k = n")
        void shouldReturnZeroWhenKEqualsN() {
            assertEquals(0.0, Combinatorics.logChoose(10, 10));
        }

        @Test
        @DisplayName("matches exp(logChoose(n,k)) = choose(n,k) for small n,k")
        void shouldMatchExponentialOfChoose() {
            assertEquals(10, Math.exp(Combinatorics.logChoose(5, 2)), EPSILON);
            assertEquals(120, Math.exp(Combinatorics.logChoose(10, 3)), EPSILON);
        }

        @Test
        @DisplayName("works for large n without overflow")
        void shouldHandleLargeN() {
            // log(C(100, 50)) ≈ 66.784
            double result = Combinatorics.logChoose(100, 50);
            assertEquals(66.78384165201743, result, EPSILON);
        }

        @Test
        @DisplayName("throws exception for negative k")
        void shouldThrowForNegativeK() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Combinatorics.logChoose(5, -1));
            assertEquals("k must be non-negative, got: -1", exception.getMessage());
        }
    }
}