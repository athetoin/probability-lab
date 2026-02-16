package com._4meonweb.probability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vector")
class VectorTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("rejects null stream")
        void shouldRejectNullStream() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new Vector((Stream<Double>) null));
            assertEquals("values cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("rejects stream with null elements")
        void shouldRejectStreamWithNullElements() {
            Stream<Double> values = Stream.of(1.0, null);
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Vector(values));
            assertEquals("values must not contain null elements", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Stream creation")
    class StreamCreationTests {

        @Test
        @DisplayName("creates vector from stream")
        void shouldCreateVectorFromStream() {
            Vector vector = new Vector(Stream.of(1.0, 2.0, 3.0));
            assertThat(vector.stream()).containsExactly(1.0, 2.0, 3.0);
        }
    }

    @Nested
    @DisplayName("Basic operations")
    class BasicOperationTests {

        @Test
        @DisplayName("sum returns 0 for empty vector")
        void sumShouldHandleEmptyVector() {
            assertEquals(0.0, new Vector(Stream.empty()).sum());
        }

        @Test
        @DisplayName("max rejects empty vector")
        void maxShouldRejectEmptyVector() {
            Vector empty = new Vector(Stream.empty());
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    empty::max);
            assertTrue(exception.getMessage().contains("Vector is empty"));
        }

        @Test
        @DisplayName("min rejects empty vector")
        void minShouldRejectEmptyVector() {
            Vector empty = new Vector(Stream.empty());
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    empty::min);
            assertTrue(exception.getMessage().contains("Vector is empty"));
        }

        @Test
        @DisplayName("length returns vector size")
        void lengthShouldReturnVectorSize() {
            assertEquals(3, Vector.of(1, 2, 3).length());
        }
    }

    @Nested
    @DisplayName("Stream access")
    class StreamAccessTests {

        @Test
        @DisplayName("stream returns DoubleStream of values")
        void shouldReturnDoubleStream() {
            Vector vec = Vector.of(1, 2, 3);
            double sum = vec.stream().sum();
            assertEquals(6.0, sum);
        }

        @Test
        @DisplayName("stream allows lazy processing")
        void shouldAllowLazyProcessing() {
            Vector vec = Vector.of(1, 2, 3, 4, 5);
            double result = vec.stream()
                    .filter(x -> x > 2)
                    .map(x -> x * 2)
                    .sum();
            assertEquals(24.0, result); // (3 + 4 + 5) * 2
        }

        @Test
        @DisplayName("stream can be collected back to vector")
        void shouldCollectBackToVector() {
            Vector vec = Vector.of(1, 2, 3);
            Vector doubled = new Vector(
                    vec.stream()
                            .map(x -> x * 2)
                            .boxed());
            assertThat(doubled.stream()).containsExactly(2.0, 4.0, 6.0);
        }
    }

    @Nested
    @DisplayName("Subvector extraction")
    class SubvectorTests {

        @Test
        @DisplayName("slice returns inclusive range")
        void sliceShouldReturnInclusiveRange() {
            Vector vector = Vector.of(1, 2, 3, 4, 5);
            assertThat(vector.slice(2, 4).stream()).containsExactly(2.0, 3.0, 4.0);
        }

        @Test
        @DisplayName("slice rejects invalid bounds")
        void sliceShouldRejectInvalidBounds() {
            Vector vector = Vector.of(1, 2, 3);
            assertThrows(IllegalArgumentException.class, () -> vector.slice(0, 2));
            assertThrows(IllegalArgumentException.class, () -> vector.slice(2, 4));
            assertThrows(IllegalArgumentException.class, () -> vector.slice(3, 2));
        }

        @Test
        @DisplayName("at returns 1-based element")
        void atShouldReturnElement() {
            Vector vector = Vector.of(10, 20, 30);
            assertEquals(20.0, vector.at(2));
        }

        @Test
        @DisplayName("at rejects invalid index")
        void atShouldRejectInvalidIndex() {
            Vector vector = Vector.of(10, 20, 30);
            assertThrows(IllegalArgumentException.class, () -> vector.at(0));
            assertThrows(IllegalArgumentException.class, () -> vector.at(4));
        }
    }

    @Nested
    @DisplayName("Exclusion")
    class ExclusionTests {

        @Test
        @DisplayName("excludeIndex removes element")
        void excludeIndexShouldRemoveElement() {
            Vector vector = Vector.of(1, 2, 3, 4);
            assertThat(vector.excludeIndex(2).stream()).containsExactly(1.0, 3.0, 4.0);
        }

        @Test
        @DisplayName("excludeIndices removes multiple elements")
        void excludeIndicesShouldRemoveMultipleElements() {
            Vector vector = Vector.of(1, 2, 3, 4, 5);
            assertThat(vector.excludeIndices(List.of(4, 2, 2)).stream()).containsExactly(1.0, 3.0, 5.0);
        }

        @Test
        @DisplayName("excludeIndices rejects null index list")
        void excludeIndicesShouldRejectNullList() {
            Vector vector = Vector.of(1, 2, 3);
            assertThrows(NullPointerException.class, () -> vector.excludeIndices(null));
        }

        @Test
        @DisplayName("excludeIndices rejects null indices")
        void excludeIndicesShouldRejectNullIndex() {
            Vector vector = Vector.of(1, 2, 3);
            List<Integer> indices = java.util.Arrays.asList(1, null);
            assertThrows(IllegalArgumentException.class, () -> vector.excludeIndices(indices));
        }
    }

    @Nested
    @DisplayName("Vectorized arithmetic")
    class VectorizedArithmeticTests {

        @Test
        @DisplayName("add supports recycling")
        void addShouldRecycle() {
            Vector xs = Vector.of(1, 2, 3, 4);
            Vector ys = Vector.of(10, 20);
            assertThat(xs.add(ys).stream()).containsExactly(11.0, 22.0, 13.0, 24.0);
        }

        @Test
        @DisplayName("subtract supports recycling")
        void subtractShouldRecycle() {
            Vector xs = Vector.of(5, 6, 7);
            Vector ys = Vector.of(1);
            assertThat(xs.subtract(ys).stream()).containsExactly(4.0, 5.0, 6.0);
        }

        @Test
        @DisplayName("multiply supports recycling")
        void multiplyShouldRecycle() {
            Vector xs = Vector.of(2, 3, 4, 5);
            Vector ys = Vector.of(2, 4);
            assertThat(xs.multiply(ys).stream()).containsExactly(4.0, 12.0, 8.0, 20.0);
        }

        @Test
        @DisplayName("divide supports recycling")
        void divideShouldRecycle() {
            Vector xs = Vector.of(10, 20, 30, 40);
            Vector ys = Vector.of(2);
            assertThat(xs.divide(ys).stream()).containsExactly(5.0, 10.0, 15.0, 20.0);
        }

        @Test
        @DisplayName("rejects incompatible lengths")
        void shouldRejectIncompatibleLengths() {
            Vector xs = Vector.of(1, 2, 3);
            Vector ys = Vector.of(10, 20);
            assertThrows(IllegalArgumentException.class, () -> xs.add(ys));
        }

        @Test
        @DisplayName("rejects empty vectors")
        void shouldRejectEmptyVectors() {
            Vector empty = new Vector(Stream.empty());
            Vector xs = Vector.of(1, 2, 3);
            assertThrows(IllegalArgumentException.class, () -> empty.add(xs));
            assertThrows(IllegalArgumentException.class, () -> xs.add(empty));
        }
    }

    @Nested
    @DisplayName("Recycle helper")
    class RecycleHelperTests {

        @ParameterizedTest
        @CsvSource({
                "2,4",
                "3,6"
        })
        @DisplayName("recycles to target length")
        void recycleShouldRepeatToTargetLength(int baseLength, int targetLength) {
            Vector base = Vector.of(IntStream.rangeClosed(1, baseLength).asDoubleStream().toArray());
            Vector recycled = base.recycle(targetLength);
            assertEquals(targetLength, recycled.length());
        }

        @Test
        @DisplayName("recycle rejects incompatible target length")
        void recycleShouldRejectIncompatibleTargetLength() {
            Vector base = Vector.of(1, 2);
            assertThrows(IllegalArgumentException.class, () -> base.recycle(3));
        }

        @Test
        @DisplayName("recycle rejects non-zero target when empty")
        void recycleShouldRejectNonZeroTargetWhenEmpty() {
            Vector empty = new Vector(Stream.empty());
            assertThrows(IllegalArgumentException.class, () -> empty.recycle(1));
        }

        @Test
        @DisplayName("recycle allows zero target length")
        void recycleShouldAllowZeroTargetLength() {
            Vector empty = new Vector(Stream.empty());
            assertEquals(0, empty.recycle(0).length());
        }
    }

    @Nested
    @DisplayName("Tabulate")
    class TabulateTests {

        @Test
        @DisplayName("counts frequency of positive integer values")
        void shouldCountFrequencies() {
            Vector vec = Vector.of(1, 2, 1, 3, 2, 1);
            Vector result = vec.tabulate();
            assertThat(result.stream()).containsExactly(3.0, 2.0, 1.0);
        }

        @Test
        @DisplayName("handles single value")
        void shouldHandleSingleValue() {
            Vector vec = Vector.of(5, 5, 5);
            Vector result = vec.tabulate();
            assertThat(result.stream()).containsExactly(0.0, 0.0, 0.0, 0.0, 3.0);
        }

        @Test
        @DisplayName("ignores values less than 1")
        void shouldIgnoreValuesLessThanOne() {
            Vector vec = Vector.of(1, 0, -1, 2, 0.5, 3);
            Vector result = vec.tabulate();
            assertThat(result.stream()).containsExactly(1.0, 1.0, 1.0);
        }

        @Test
        @DisplayName("handles consecutive values starting from 1")
        void shouldHandleConsecutiveValues() {
            Vector vec = Vector.of(1, 1, 2, 2, 2, 3);
            Vector result = vec.tabulate();
            assertThat(result.stream()).containsExactly(2.0, 3.0, 1.0);
        }

        @Test
        @DisplayName("throws exception for empty vector")
        void shouldThrowForEmptyVector() {
            Vector empty = new Vector(Stream.empty());
            assertThrows(IllegalArgumentException.class, empty::tabulate);
        }

        @Test
        @DisplayName("throws exception when no positive values")
        void shouldThrowForNoPositiveValues() {
            Vector vec = Vector.of(0, -1, -2, 0.5);
            assertThrows(IllegalArgumentException.class, vec::tabulate);
        }

        @Test
        @DisplayName("uses 1-based indexing in result")
        void shouldUseOneBasedIndexing() {
            Vector vec = Vector.of(1, 2, 1, 3, 2, 1);
            Vector result = vec.tabulate();
            assertEquals(3.0, result.at(1)); // value 1 appears 3 times
            assertEquals(2.0, result.at(2)); // value 2 appears 2 times
            assertEquals(1.0, result.at(3)); // value 3 appears 1 time
        }
    }
}
