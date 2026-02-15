package com._4meonweb.probability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Immutable numeric vector with 1-based indexing and R-style recycling rules.
 */
public final class Vector {
    private final List<Double> xs;

    /**
     * Creates a vector from a list of numbers. The list is defensively copied.
     *
     * @param xs values to store
     * @throws NullPointerException     if xs is null
     * @throws IllegalArgumentException if xs contains null values
     */
    public Vector(List<Double> xs) {
        Objects.requireNonNull(xs, "xs cannot be null");
        if (xs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("xs must not contain null values");
        }
        this.xs = List.copyOf(xs);
    }

    /**
     * Creates a vector from primitive values.
     *
     * @param values values to store
     * @return a new vector containing the provided values
     * @throws NullPointerException if values is null
     */
    public static Vector of(double... values) {
        Objects.requireNonNull(values, "values cannot be null");
        return new Vector(DoubleStream.of(values).boxed().toList());
    }

    /**
     * Creates a vector from a stream of boxed values.
     *
     * @param values stream of values to store
     * @return a new vector containing the streamed values
     * @throws NullPointerException if values is null
     */
    public static Vector fromStream(Stream<Double> values) {
        Objects.requireNonNull(values, "values cannot be null");
        return new Vector(values.toList());
    }

    /**
     * Returns an immutable view of the stored values.
     *
     * @return immutable list view of values
     */
    public List<Double> toList() {
        return Collections.unmodifiableList(xs);
    }

    /**
     * Computes the sum of all values.
     *
     * @return sum of values
     */
    public double sum() {
        return xs.stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * Returns the maximum value.
     *
     * @return maximum value
     * @throws IllegalArgumentException if the vector is empty
     */
    public double max() {
        ensureNotEmpty("max");
        return xs.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
    }

    /**
     * Returns the minimum value.
     *
     * @return minimum value
     * @throws IllegalArgumentException if the vector is empty
     */
    public double min() {
        ensureNotEmpty("min");
        return xs.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
    }

    /**
     * Returns the number of values.
     *
     * @return vector length
     */
    public int length() {
        return xs.size();
    }

    /**
     * Extracts a 1-based inclusive slice.
     *
     * @param from starting index (1-based, inclusive)
     * @param to   ending index (1-based, inclusive)
     * @return sliced vector
     * @throws IllegalArgumentException if the bounds are invalid
     */
    public Vector slice(int from, int to) {
        ensureValidSlice(from, to);
        return new Vector(xs.subList(from - 1, to));
    }

    /**
     * Returns a 1-based element.
     *
     * @param index index to read (1-based)
     * @return element at the given index
     * @throws IllegalArgumentException if the index is invalid
     */
    public double at(int index) {
        ensureValidIndex(index);
        return xs.get(index - 1);
    }

    /**
     * Returns a new vector without the element at the 1-based index.
     *
     * @param index index to exclude (1-based)
     * @return vector without the specified element
     * @throws IllegalArgumentException if the index is invalid
     */
    public Vector excludeIndex(int index) {
        ensureValidIndex(index);
        var copy = new ArrayList<>(xs);
        copy.remove(index - 1);
        return new Vector(copy);
    }

    /**
     * Returns a new vector without the elements at the 1-based indices.
     *
     * @param indices indices to exclude (1-based)
     * @return vector without the specified elements
     * @throws NullPointerException     if indices is null
     * @throws IllegalArgumentException if any index is null or invalid
     */
    public Vector excludeIndices(List<Integer> indices) {
        Objects.requireNonNull(indices, "indices cannot be null");
        if (indices.isEmpty()) {
            return this;
        }
        var toRemove = new HashSet<Integer>();
        for (Integer index : indices) {
            if (index == null) {
                throw new IllegalArgumentException("indices must not contain null values");
            }
            ensureValidIndex(index);
            toRemove.add(index);
        }
        var filtered = IntStream.range(0, xs.size())
                .filter(i -> !toRemove.contains(i + 1))
                .mapToObj(xs::get)
                .toList();
        return new Vector(filtered);
    }

    /**
     * Adds another vector using recycling.
     *
     * @param ys vector to add
     * @return sum of the two vectors
     * @throws NullPointerException     if ys is null
     * @throws IllegalArgumentException if either vector is empty or lengths are
     *                                  incompatible
     */
    public Vector add(Vector ys) {
        return combine(ys, (a, b) -> a + b);
    }

    /**
     * Subtracts another vector using recycling.
     *
     * @param ys vector to subtract
     * @return difference of the two vectors
     * @throws NullPointerException     if ys is null
     * @throws IllegalArgumentException if either vector is empty or lengths are
     *                                  incompatible
     */
    public Vector subtract(Vector ys) {
        return combine(ys, (a, b) -> a - b);
    }

    /**
     * Multiplies by another vector using recycling.
     *
     * @param ys vector to multiply
     * @return product of the two vectors
     * @throws NullPointerException     if ys is null
     * @throws IllegalArgumentException if either vector is empty or lengths are
     *                                  incompatible
     */
    public Vector multiply(Vector ys) {
        return combine(ys, (a, b) -> a * b);
    }

    /**
     * Divides by another vector using recycling.
     *
     * @param ys vector to divide by
     * @return quotient of the two vectors
     * @throws NullPointerException     if ys is null
     * @throws IllegalArgumentException if either vector is empty or lengths are
     *                                  incompatible
     */
    public Vector divide(Vector ys) {
        return combine(ys, (a, b) -> a / b);
    }

    /**
     * Repeats values to the requested length, requiring an exact multiple.
     *
     * @param targetLength desired length
     * @return recycled vector
     * @throws IllegalArgumentException if targetLength is negative, not a multiple,
     *                                  or the vector is empty
     */
    public Vector recycle(int targetLength) {
        if (targetLength < 0) {
            throw new IllegalArgumentException("targetLength must be non-negative, got: " + targetLength);
        }
        if (targetLength == 0) {
            return new Vector(List.of());
        }
        ensureNotEmpty("recycle");
        if (targetLength % xs.size() != 0) {
            throw new IllegalArgumentException("targetLength must be a multiple of vector length");
        }
        var recycled = IntStream.range(0, targetLength)
                .mapToObj(i -> xs.get(i % xs.size()))
                .toList();
        return new Vector(recycled);
    }

    private Vector combine(Vector ys, DoubleBinaryOperator op) {
        Objects.requireNonNull(ys, "ys cannot be null");
        ensureNotEmpty("vectorized operation");
        ys.ensureNotEmpty("vectorized operation");
        int targetLength = Math.max(xs.size(), ys.xs.size());
        if (targetLength % xs.size() != 0 || targetLength % ys.xs.size() != 0) {
            throw new IllegalArgumentException("Lengths are not compatible for recycling");
        }
        var combined = IntStream.range(0, targetLength)
                .mapToDouble(i -> op.applyAsDouble(xs.get(i % xs.size()), ys.xs.get(i % ys.xs.size())))
                .boxed()
                .toList();
        return new Vector(combined);
    }

    private void ensureValidIndex(int index) {
        if (index < 1 || index > xs.size()) {
            throw new IllegalArgumentException("index must be in [1, " + xs.size() + "] got: " + index);
        }
    }

    private void ensureValidSlice(int from, int to) {
        if (from < 1 || to < 1 || from > to || to > xs.size()) {
            throw new IllegalArgumentException("slice bounds must be within [1, " + xs.size() + "] and from <= to");
        }
    }

    private void ensureNotEmpty(String operation) {
        if (xs.isEmpty()) {
            throw new IllegalArgumentException("Vector is empty for operation: " + operation);
        }
    }

    @FunctionalInterface
    private interface DoubleBinaryOperator {
        double applyAsDouble(double left, double right);
    }
}
