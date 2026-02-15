package com._4meonweb.probability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vector")
class VectorTest {

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("rejects null list")
        void shouldRejectNullList() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> new Vector(null));
            assertEquals("xs cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("rejects null element")
        void shouldRejectNullElement() {
            List<Double> values = java.util.Arrays.asList(1.0, null);
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Vector(values));
            assertEquals("xs must not contain null values", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Stream factory")
    class StreamFactoryTests {

        @Test
        @DisplayName("creates vector from stream")
        void shouldCreateVectorFromStream() {
            Vector vector = Vector.fromStream(Stream.of(1.0, 2.0, 3.0));
            assertEquals(List.of(1.0, 2.0, 3.0), vector.toList());
        }

        @Test
        @DisplayName("rejects null stream")
        void shouldRejectNullStream() {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> Vector.fromStream(null));
            assertEquals("values cannot be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Basic operations")
    class BasicOperationTests {

        @Test
        @DisplayName("sum returns 0 for empty vector")
        void sumShouldHandleEmptyVector() {
            assertEquals(0.0, new Vector(List.of()).sum());
        }

        @Test
        @DisplayName("max rejects empty vector")
        void maxShouldRejectEmptyVector() {
            Vector empty = new Vector(List.of());
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    empty::max);
            assertTrue(exception.getMessage().contains("Vector is empty"));
        }

        @Test
        @DisplayName("min rejects empty vector")
        void minShouldRejectEmptyVector() {
            Vector empty = new Vector(List.of());
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
    @DisplayName("Subvector extraction")
    class SubvectorTests {

        @Test
        @DisplayName("slice returns inclusive range")
        void sliceShouldReturnInclusiveRange() {
            Vector vector = Vector.of(1, 2, 3, 4, 5);
            assertEquals(List.of(2.0, 3.0, 4.0), vector.slice(2, 4).toList());
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
            assertEquals(List.of(1.0, 3.0, 4.0), vector.excludeIndex(2).toList());
        }

        @Test
        @DisplayName("excludeIndices removes multiple elements")
        void excludeIndicesShouldRemoveMultipleElements() {
            Vector vector = Vector.of(1, 2, 3, 4, 5);
            assertEquals(List.of(1.0, 3.0, 5.0), vector.excludeIndices(List.of(4, 2, 2)).toList());
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
            assertEquals(List.of(11.0, 22.0, 13.0, 24.0), xs.add(ys).toList());
        }

        @Test
        @DisplayName("subtract supports recycling")
        void subtractShouldRecycle() {
            Vector xs = Vector.of(5, 6, 7);
            Vector ys = Vector.of(1);
            assertEquals(List.of(4.0, 5.0, 6.0), xs.subtract(ys).toList());
        }

        @Test
        @DisplayName("multiply supports recycling")
        void multiplyShouldRecycle() {
            Vector xs = Vector.of(2, 3, 4, 5);
            Vector ys = Vector.of(2, 4);
            assertEquals(List.of(4.0, 12.0, 8.0, 20.0), xs.multiply(ys).toList());
        }

        @Test
        @DisplayName("divide supports recycling")
        void divideShouldRecycle() {
            Vector xs = Vector.of(10, 20, 30, 40);
            Vector ys = Vector.of(2);
            assertEquals(List.of(5.0, 10.0, 15.0, 20.0), xs.divide(ys).toList());
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
            Vector empty = new Vector(List.of());
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
            Vector empty = new Vector(List.of());
            assertThrows(IllegalArgumentException.class, () -> empty.recycle(1));
        }

        @Test
        @DisplayName("recycle allows zero target length")
        void recycleShouldAllowZeroTargetLength() {
            Vector empty = new Vector(List.of());
            assertEquals(0, empty.recycle(0).length());
        }
    }
}
