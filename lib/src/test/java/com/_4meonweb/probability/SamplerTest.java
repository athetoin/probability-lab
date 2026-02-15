package com._4meonweb.probability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sampler")
class SamplerTest {

    private Sampler sampler;

    @BeforeEach
    void setUp() {
        // Use a fixed seed for deterministic tests
        sampler = new Sampler(RandomGenerator.of("L64X128MixRandom"));
    }

    private static void assertAllInRange(List<Integer> values, int n) {
        for (Integer value : values) {
            assertTrue(value >= 1 && value <= n,
                    "Value " + value + " should be in range [1, " + n + "]");
        }
    }

    private static <T> void assertAllInList(List<T> values, List<T> source) {
        values.forEach(value -> assertTrue(source.contains(value)));
    }

    private static void assertEmpty(List<?> values) {
        assertEquals(0, values.size());
    }

    private static Stream<Arguments> emptySampleProviders() {
        return Stream.of(
                Arguments.of("sampleWithReplacement (int)",
                        (Supplier<Stream<?>>) () -> new Sampler(RandomGenerator.of("L64X128MixRandom"))
                                .sampleWithReplacement(10, 0)),
                Arguments.of("sampleWithoutReplacement (int)",
                        (Supplier<Stream<?>>) () -> new Sampler(RandomGenerator.of("L64X128MixRandom"))
                                .sampleWithoutReplacement(10, 0)),
                Arguments.of("sampleWithReplacement (List)",
                        (Supplier<Stream<?>>) () -> new Sampler(RandomGenerator.of("L64X128MixRandom"))
                                .sampleWithReplacement(List.of("a", "b"), 0)),
                Arguments.of("sampleWithoutReplacement (List)",
                        (Supplier<Stream<?>>) () -> new Sampler(RandomGenerator.of("L64X128MixRandom"))
                                .sampleWithoutReplacement(List.of("a", "b"), 0)));
    }

    @ParameterizedTest(name = "{0} returns empty stream when k is 0")
    @MethodSource("emptySampleProviders")
    void shouldReturnEmptyStreamWhenKIsZero(String label, Supplier<Stream<?>> sampleSupplier) {
        List<?> result = sampleSupplier.get().toList();
        assertEmpty(result);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("should throw NullPointerException when RandomGenerator is null")
        void shouldRejectNullRandomGenerator() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new Sampler(null));
            assertEquals("RandomGenerator cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("should create Sampler with valid RandomGenerator")
        void shouldCreateSamplerWithValidRandomGenerator() {
            RandomGenerator testRng = RandomGenerator.of("L64X128MixRandom");
            assertDoesNotThrow(() -> new Sampler(testRng));
        }
    }

    @Nested
    @DisplayName("sampleWithReplacement")
    class SampleWithReplacementTests {

        @Test
        @DisplayName("should generate k samples")
        void shouldGenerateKSamples() {
            List<Integer> result = sampler.sampleWithReplacement(10, 5)
                    .toList();
            assertEquals(5, result.size());
        }

        @Test
        @DisplayName("should generate samples in range [1, n]")
        void shouldGenerateSamplesInRange() {
            int n = 10;
            List<Integer> result = sampler.sampleWithReplacement(n, 100)
                    .toList();

            assertAllInRange(result, n);
        }

        @Test
        @DisplayName("should allow duplicates")
        void shouldAllowDuplicates() {
            // With enough samples, we should see duplicates
            List<Integer> result = sampler.sampleWithReplacement(5, 100)
                    .toList();

            long distinctCount = result.stream().distinct().count();
            assertTrue(distinctCount < result.size(),
                    "Expected duplicates in sample with replacement");
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -10 })
        @DisplayName("should throw IllegalArgumentException when n is not positive")
        void shouldRejectNonPositiveN(int n) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithReplacement(n, 5));
            assertTrue(exception.getMessage().contains("n must be positive"));
        }

        @ParameterizedTest
        @ValueSource(ints = { -1, -10 })
        @DisplayName("should throw IllegalArgumentException when k is negative")
        void shouldRejectNegativeK(int k) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithReplacement(10, k));
            assertTrue(exception.getMessage().contains("k must be non-negative"));
        }

        @ParameterizedTest
        @CsvSource({ "1, 1", "10, 5", "100, 1", "5, 20" })
        @DisplayName("should work with various valid (n, k) combinations")
        void shouldWorkWithVariousValidCombinations(int n, int k) {
            List<Integer> result = sampler.sampleWithReplacement(n, k)
                    .toList();
            assertEquals(k, result.size());
            assertAllInRange(result, n);
        }

        @Test
        @DisplayName("should work when k > n (with replacement allows this)")
        void shouldWorkWhenKGreaterThanN() {
            List<Integer> result = sampler.sampleWithReplacement(3, 10)
                    .toList();
            assertEquals(10, result.size());
        }
    }

    @Nested
    @DisplayName("sampleWithoutReplacement")
    class SampleWithoutReplacementTests {

        @Test
        @DisplayName("should generate k unique samples")
        void shouldGenerateKUniqueSamples() {
            List<Integer> result = sampler.sampleWithoutReplacement(10, 5)
                    .toList();

            assertEquals(5, result.size());
            long distinctCount = result.stream().distinct().count();
            assertEquals(5, distinctCount, "All samples should be unique");
        }

        @Test
        @DisplayName("should generate samples in range [1, n]")
        void shouldGenerateSamplesInRange() {
            int n = 10;
            List<Integer> result = sampler.sampleWithoutReplacement(n, 5)
                    .toList();

            assertAllInRange(result, n);
        }

        @Test
        @DisplayName("should not contain duplicates")
        void shouldNotContainDuplicates() {
            List<Integer> result = sampler.sampleWithoutReplacement(20, 15)
                    .toList();

            long distinctCount = result.stream().distinct().count();
            assertEquals(result.size(), distinctCount,
                    "Should not contain any duplicates");
        }

        @Test
        @DisplayName("should work when k equals n")
        void shouldWorkWhenKEqualsN() {
            List<Integer> result = sampler.sampleWithoutReplacement(5, 5)
                    .toList();

            assertEquals(5, result.size());
            assertTrue(result.containsAll(List.of(1, 2, 3, 4, 5)),
                    "Should contain all values from 1 to n");
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -10 })
        @DisplayName("should throw IllegalArgumentException when n is not positive")
        void shouldRejectNonPositiveN(int n) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(n, 5));
            assertTrue(exception.getMessage().contains("n must be positive"));
        }

        @ParameterizedTest
        @ValueSource(ints = { -1, -10 })
        @DisplayName("should throw IllegalArgumentException when k is negative")
        void shouldRejectNegativeK(int k) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(10, k));
            assertTrue(exception.getMessage().contains("k must be non-negative"));
        }

        @ParameterizedTest
        @CsvSource({ "5, 6", "10, 11", "3, 100" })
        @DisplayName("should throw IllegalArgumentException when k > n")
        void shouldRejectKGreaterThanN(int n, int k) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(n, k));
            assertTrue(exception.getMessage().contains("k cannot be greater than n"));
        }
    }

    @Nested
    @DisplayName("sampleWithReplacement (List)")
    class SampleWithReplacementListTests {

        @Test
        @DisplayName("should generate k samples")
        void shouldGenerateKSamples() {
            List<String> result = sampler.sampleWithReplacement(List.of("a", "b", "c"), 5)
                    .toList();
            assertEquals(5, result.size());
        }

        @Test
        @DisplayName("should generate samples from the provided list")
        void shouldGenerateSamplesFromList() {
            List<String> source = List.of("a", "b", "c");
            List<String> result = sampler.sampleWithReplacement(source, 20)
                    .toList();

            assertAllInList(result, source);
        }

        @Test
        @DisplayName("should allow duplicates")
        void shouldAllowDuplicates() {
            List<String> result = sampler.sampleWithReplacement(List.of("a", "b"), 50)
                    .toList();

            long distinctCount = result.stream().distinct().count();
            assertTrue(distinctCount < result.size());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when k is negative")
        void shouldRejectNegativeK() {
            List<String> source = List.of("a");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithReplacement(source, -1));
            assertTrue(exception.getMessage().contains("k must be non-negative"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when list is empty and k is positive")
        void shouldRejectEmptyListWhenKIsPositive() {
            List<String> source = List.of();
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithReplacement(source, 1));
            assertTrue(exception.getMessage().contains("xs must not be empty"));
        }

        @Test
        @DisplayName("should throw NullPointerException when list is null")
        void shouldRejectNullList() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> sampler.sampleWithReplacement(null, 1));
            assertEquals("xs cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("sampleWithoutReplacement (List)")
    class SampleWithoutReplacementListTests {

        @Test
        @DisplayName("should generate k unique samples")
        void shouldGenerateKUniqueSamples() {
            List<String> result = sampler.sampleWithoutReplacement(List.of("a", "b", "c", "d"), 3)
                    .toList();

            assertEquals(3, result.size());
            assertEquals(3, result.stream().distinct().count());
        }

        @Test
        @DisplayName("should generate samples from the provided list")
        void shouldGenerateSamplesFromList() {
            List<String> source = List.of("a", "b", "c", "d");
            List<String> result = sampler.sampleWithoutReplacement(source, 3)
                    .toList();

            assertAllInList(result, source);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when k is negative")
        void shouldRejectNegativeK() {
            List<String> source = List.of("a");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(source, -1));
            assertTrue(exception.getMessage().contains("k must be non-negative"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when k is greater than list size")
        void shouldRejectKGreaterThanSize() {
            List<String> source = List.of("a", "b");
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(source, 3));
            assertTrue(exception.getMessage().contains("k cannot be greater than xs size"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when list is empty and k is positive")
        void shouldRejectEmptyListWhenKIsPositive() {
            List<String> source = List.of();
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.sampleWithoutReplacement(source, 1));
            assertTrue(exception.getMessage().contains("xs must not be empty"));
        }

        @Test
        @DisplayName("should throw NullPointerException when list is null")
        void shouldRejectNullList() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> sampler.sampleWithoutReplacement(null, 1));
            assertEquals("xs cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("LETTERS")
    class LettersTests {

        @Test
        @DisplayName("should contain lowercase letters a to z")
        void shouldContainLowercaseLetters() {
            List<String> expected = IntStream.rangeClosed('a', 'z')
                    .mapToObj(c -> String.valueOf((char) c))
                    .toList();
            List<String> actual = Sampler.LETTERS;

            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("should have size 26")
        void shouldHaveSize26() {
            assertEquals(26, Sampler.LETTERS.size());
        }
    }

    @Nested
    @DisplayName("permutation")
    class PermutationTests {

        @Test
        @DisplayName("should generate permutation of size n")
        void shouldGeneratePermutationOfSizeN() {
            List<Integer> result = sampler.permutation(10)
                    .toList();
            assertEquals(10, result.size());
        }

        @Test
        @DisplayName("should contain all values from 1 to n")
        void shouldContainAllValuesFromOneToN() {
            int n = 10;
            List<Integer> result = sampler.permutation(n)
                    .toList();

            for (int i = 1; i <= n; i++) {
                assertTrue(result.contains(i),
                        "Permutation should contain value " + i);
            }
        }

        @Test
        @DisplayName("should not contain duplicates")
        void shouldNotContainDuplicates() {
            List<Integer> result = sampler.permutation(20)
                    .toList();

            long distinctCount = result.stream().distinct().count();
            assertEquals(result.size(), distinctCount,
                    "Permutation should not contain duplicates");
        }

        @Test
        @DisplayName("should produce different orderings on multiple calls")
        void shouldProduceDifferentOrderings() {
            List<Integer> perm1 = sampler.permutation(10)
                    .toList();
            List<Integer> perm2 = sampler.permutation(10)
                    .toList();

            // While theoretically they could be the same, it's extremely unlikely for n=10
            assertNotEquals(perm1, perm2,
                    "Multiple calls should produce different orderings");
        }

        @Test
        @DisplayName("should work with small values")
        void shouldWorkWithSmallValues() {
            List<Integer> result = sampler.permutation(1)
                    .toList();

            assertEquals(1, result.size());
            assertEquals(1, result.get(0));
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -10 })
        @DisplayName("should throw IllegalArgumentException when n is not positive")
        void shouldRejectNonPositiveN(int n) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> sampler.permutation(n));
            assertTrue(exception.getMessage().contains("n must be positive"));
        }

        @ParameterizedTest
        @ValueSource(ints = { 1, 5, 10, 50 })
        @DisplayName("should work with various valid n values")
        void shouldWorkWithVariousValidValues(int n) {
            List<Integer> result = sampler.permutation(n)
                    .toList();

            assertEquals(n, result.size());
            assertEquals(n, result.stream().distinct().count());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("should produce reproducible results with same seed")
        void shouldProduceReproducibleResults() {
            RandomGenerator rng1 = new Random(42);
            Sampler sampler1 = new Sampler(rng1);

            RandomGenerator rng2 = new Random(42);
            Sampler sampler2 = new Sampler(rng2);

            List<Integer> result1 = sampler1.sampleWithReplacement(100, 10)
                    .toList();
            List<Integer> result2 = sampler2.sampleWithReplacement(100, 10)
                    .toList();

            assertEquals(result1, result2,
                    "Same seed should produce same results");
        }

        @Test
        @DisplayName("should handle large values efficiently")
        void shouldHandleLargeValuesEfficiently() {
            assertTimeout(java.time.Duration.ofSeconds(1), () -> {
                sampler.sampleWithReplacement(10000, 1000)
                        .toList();
                sampler.sampleWithoutReplacement(10000, 1000)
                        .toList();
                sampler.permutation(1000)
                        .toList();
            });
        }
    }
}
