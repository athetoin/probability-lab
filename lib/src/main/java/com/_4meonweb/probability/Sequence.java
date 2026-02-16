package com._4meonweb.probability;

import java.util.Collection;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Sequence utilities inspired by R's sequence functions.
 * All methods return Stream types for functional composition.
 */
public class Sequence {

    private Sequence() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generate an inclusive range of integers.
     * Supports both increasing and decreasing sequences.
     * Equivalent to R's from:to
     *
     * @param from start value (inclusive)
     * @param to   end value (inclusive)
     * @return Stream of integers
     */
    public static Stream<Integer> range(int from, int to) {
        if (from <= to) {
            return IntStream.rangeClosed(from, to).boxed();
        } else {
            return IntStream.iterate(from, i -> i >= to, i -> i - 1).boxed();
        }
    }

    /**
     * Generate sequence with a step size.
     * Equivalent to R's seq(from, to, by=step)
     * Includes 'to' only if step lands exactly on it.
     *
     * @param from start value
     * @param to   end value
     * @param step step size (positive or negative)
     * @return Stream of doubles
     */
    public static Stream<Double> seqBy(double from, double to, double step) {
        if (Double.compare(step, 0.0) == 0) {
            throw new IllegalArgumentException("step must be non-zero");
        }

        // Check if step direction matches range direction
        boolean increasing = to > from;
        boolean positiveStep = step > 0;

        if (increasing != positiveStep) {
            // Step direction doesn't match range direction
            return Stream.empty();
        }

        return DoubleStream.iterate(from,
                val -> step > 0 ? val <= to : val >= to,
                val -> val + step)
                .boxed();
    }

    /**
     * Generate equally spaced sequence of specified length.
     * Equivalent to R's seq(from, to, length.out=n)
     *
     * @param from   start value
     * @param to     end value
     * @param length number of values to generate
     * @return Stream of doubles
     */
    public static Stream<Double> seqLength(double from, double to, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative, got: " + length);
        }

        if (length == 0) {
            return Stream.empty();
        }

        if (length == 1) {
            return Stream.of(from);
        }

        var step = (to - from) / (length - 1);
        return IntStream.range(0, length)
                .mapToDouble(i -> from + i * step)
                .boxed();
    }

    /**
     * Generate sequence from 1 to n.
     * Equivalent to R's seq_len(n)
     *
     * @param n length of sequence
     * @return Stream of integers from 1 to n
     */
    public static Stream<Integer> seqLen(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative, got: " + n);
        }
        return IntStream.rangeClosed(1, n).boxed();
    }

    /**
     * Generate sequence from 1 to collection size.
     * Equivalent to R's seq_along(xs)
     *
     * @param xs  collection
     * @param <T> element type
     * @return Stream of integers from 1 to xs.size()
     */
    public static <T> Stream<Integer> seqAlong(Collection<T> xs) {
        if (xs == null) {
            throw new IllegalArgumentException("collection must not be null");
        }
        return seqLen(xs.size());
    }

    /**
     * Repeat a value n times.
     * Equivalent to R's rep(value, times=times)
     *
     * @param value value to repeat
     * @param times number of repetitions
     * @param <T>   value type
     * @return Stream containing value repeated times
     */
    public static <T> Stream<T> repTimes(T value, int times) {
        if (times < 0) {
            throw new IllegalArgumentException("times must be non-negative, got: " + times);
        }
        return Stream.generate(() -> value).limit(times);
    }

    /**
     * Repeat each element in collection n times.
     * Equivalent to R's rep(xs, each=each)
     *
     * @param xs   collection of elements
     * @param each number of times to repeat each element
     * @param <T>  element type
     * @return Stream with each element repeated
     */
    public static <T> Stream<T> repEach(Collection<T> xs, int each) {
        if (xs == null) {
            throw new IllegalArgumentException("collection must not be null");
        }
        if (each < 0) {
            throw new IllegalArgumentException("each must be non-negative, got: " + each);
        }
        return xs.stream()
                .flatMap(x -> Stream.generate(() -> x).limit(each));
    }

    /**
     * Repeat entire collection n times.
     * Equivalent to R's rep(xs, times=times)
     *
     * @param xs    collection to repeat
     * @param times number of times to repeat the entire collection
     * @param <T>   element type
     * @return Stream containing collection repeated times
     */
    public static <T> Stream<T> repTimes(Collection<T> xs, int times) {
        if (xs == null) {
            throw new IllegalArgumentException("collection must not be null");
        }
        if (times < 0) {
            throw new IllegalArgumentException("times must be non-negative, got: " + times);
        }
        return Stream.generate(() -> xs)
                .limit(times)
                .flatMap(Collection::stream);
    }

    /**
     * Concatenate multiple collections.
     * Equivalent to R's c(...)
     *
     * @param lists collections to concatenate
     * @param <T>   element type
     * @return Stream containing all elements
     */
    @SafeVarargs
    public static <T> Stream<T> concat(Collection<T>... lists) {
        if (lists == null) {
            throw new IllegalArgumentException("lists must not be null");
        }
        return Stream.of(lists)
                .filter(java.util.Objects::nonNull)
                .flatMap(Collection::stream);
    }
}
