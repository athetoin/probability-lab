package com._4meonweb.probability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A utility class for performing various sampling operations using a random
 * number generator.
 * Supports sampling with and without replacement, as well as generating random
 * permutations.
 */
public class Sampler {
    public static final List<String> LETTERS = List.of(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private final RandomGenerator rng;

    /**
     * Constructs a new Sampler with the specified random number generator.
     *
     * @param rng the random number generator to use for sampling operations
     * @throws NullPointerException if rng is null
     */
    public Sampler(RandomGenerator rng) {
        this.rng = Objects.requireNonNull(rng, "RandomGenerator cannot be null");
    }

    /**
     * Validates that n is positive.
     *
     * @param n the value to validate
     * @throws IllegalArgumentException if n is not positive
     */
    private void validateN(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive, got: " + n);
        }
    }

    /**
     * Validates that k is non-negative.
     *
     * @param k the value to validate
     * @throws IllegalArgumentException if k is negative
     */
    private void validateK(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative, got: " + k);
        }
    }

    /**
     * Validates that the provided list is not null.
     *
     * @param xs the list to validate
     * @throws NullPointerException if xs is null
     */
    private void validateList(List<?> xs) {
        Objects.requireNonNull(xs, "xs cannot be null");
    }

    /**
     * Creates a shuffled list of integers from 1 to n.
     *
     * @param n the upper bound of the range (inclusive)
     * @return a shuffled list containing all integers from [1, n]
     */
    private ArrayList<Integer> createShuffledList(int n) {
        var list = new ArrayList<>(IntStream.rangeClosed(1, n).boxed().toList());
        Collections.shuffle(list, rng);
        return list;
    }

    /**
     * Generates a sample of k integers from the range [1, n] with replacement.
     * Each integer can appear multiple times in the result.
     *
     * @param n the upper bound of the range (inclusive)
     * @param k the number of samples to generate
     * @return a stream of k randomly selected integers from [1, n]
     * @throws IllegalArgumentException if n is not positive or k is negative
     */
    public Stream<Integer> sampleWithReplacement(int n, int k) {
        validateN(n);
        validateK(k);
        return IntStream.range(0, k)
                .map(i -> rng.nextInt(1, n + 1))
                .boxed();
    }

    /**
     * Generates a sample of k elements from the list with replacement.
     * Elements can appear multiple times in the result.
     *
     * @param xs  the source list to sample from
     * @param k   the number of samples to generate
     * @param <T> the element type
     * @return a stream of k randomly selected elements from the list
     * @throws NullPointerException     if xs is null
     * @throws IllegalArgumentException if k is negative or xs is empty when k is
     *                                  positive
     */
    public <T> Stream<T> sampleWithReplacement(List<T> xs, int k) {
        validateK(k);
        validateList(xs);
        if (k == 0) {
            return Stream.empty();
        }
        if (xs.isEmpty()) {
            throw new IllegalArgumentException("xs must not be empty when k is positive");
        }
        return IntStream.range(0, k)
                .mapToObj(i -> xs.get(rng.nextInt(xs.size())));
    }

    /**
     * Generates a sample of k integers from the range [1, n] without replacement.
     * Each integer can appear at most once in the result.
     *
     * @param n the upper bound of the range (inclusive)
     * @param k the number of samples to generate
     * @return a stream of k randomly selected unique integers from [1, n]
     * @throws IllegalArgumentException if n is not positive, k is negative, or k >
     *                                  n
     */
    public Stream<Integer> sampleWithoutReplacement(int n, int k) {
        validateN(n);
        validateK(k);
        if (k > n) {
            throw new IllegalArgumentException("k cannot be greater than n, got k=" + k + ", n=" + n);
        }
        return createShuffledList(n).stream().limit(k);
    }

    /**
     * Generates a sample of k elements from the list without replacement.
     * Each element can appear at most once in the result.
     *
     * @param xs  the source list to sample from
     * @param k   the number of samples to generate
     * @param <T> the element type
     * @return a stream of k randomly selected unique elements from the list
     * @throws NullPointerException     if xs is null
     * @throws IllegalArgumentException if k is negative, k is greater than the list
     *                                  size,
     *                                  or xs is empty when k is positive
     */
    public <T> Stream<T> sampleWithoutReplacement(List<T> xs, int k) {
        validateK(k);
        validateList(xs);
        if (k == 0) {
            return Stream.empty();
        }
        if (xs.isEmpty()) {
            throw new IllegalArgumentException("xs must not be empty when k is positive");
        }
        if (k > xs.size()) {
            throw new IllegalArgumentException("k cannot be greater than xs size, got k=" + k + ", size=" + xs.size());
        }
        var list = new ArrayList<>(xs);
        Collections.shuffle(list, rng);
        return list.stream().limit(k);
    }

    /**
     * Generates a random permutation of integers from the range [1, n].
     *
     * @param n the upper bound of the range (inclusive)
     * @return a stream containing all integers from [1, n] in random order
     * @throws IllegalArgumentException if n is not positive
     */
    public Stream<Integer> permutation(int n) {
        validateN(n);
        return createShuffledList(n).stream();
    }

}
