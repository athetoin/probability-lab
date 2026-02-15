package com._4meonweb.probability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sequence")
class SequenceTest {

    @Nested
    @DisplayName("range")
    class RangeTests {

        @Test
        @DisplayName("generates increasing range")
        void shouldGenerateIncreasingRange() {
            var result = Sequence.range(1, 5).toList();
            assertEquals(List.of(1, 2, 3, 4, 5), result);
        }

        @Test
        @DisplayName("generates decreasing range")
        void shouldGenerateDecreasingRange() {
            var result = Sequence.range(5, 1).toList();
            assertEquals(List.of(5, 4, 3, 2, 1), result);
        }

        @Test
        @DisplayName("generates single element range")
        void shouldGenerateSingleElementRange() {
            var result = Sequence.range(3, 3).toList();
            assertEquals(List.of(3), result);
        }

        @Test
        @DisplayName("generates range with negative numbers")
        void shouldGenerateRangeWithNegatives() {
            var result = Sequence.range(-3, 2).toList();
            assertEquals(List.of(-3, -2, -1, 0, 1, 2), result);
        }

        @Test
        @DisplayName("generates decreasing range with negatives")
        void shouldGenerateDecreasingRangeWithNegatives() {
            var result = Sequence.range(2, -3).toList();
            assertEquals(List.of(2, 1, 0, -1, -2, -3), result);
        }
    }

    @Nested
    @DisplayName("seqBy")
    class SeqByTests {

        @Test
        @DisplayName("generates sequence with positive step")
        void shouldGenerateWithPositiveStep() {
            var result = Sequence.seqBy(0, 10, 2.5).toList();
            assertEquals(List.of(0.0, 2.5, 5.0, 7.5, 10.0), result);
        }

        @Test
        @DisplayName("generates sequence with negative step")
        void shouldGenerateWithNegativeStep() {
            var result = Sequence.seqBy(10, 0, -2.5).toList();
            assertEquals(List.of(10.0, 7.5, 5.0, 2.5, 0.0), result);
        }

        @Test
        @DisplayName("excludes 'to' when step doesn't land on it")
        void shouldExcludeToWhenStepDoesntMatch() {
            var result = Sequence.seqBy(0, 10, 3).toList();
            assertEquals(List.of(0.0, 3.0, 6.0, 9.0), result);
        }

        @Test
        @DisplayName("returns empty when step direction doesn't match range")
        void shouldReturnEmptyWhenStepDirectionDoesntMatch() {
            var result = Sequence.seqBy(0, 10, -1).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty when negative step with increasing range")
        void shouldReturnEmptyForNegativeStepIncreasingRange() {
            var result = Sequence.seqBy(10, 0, 1).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("throws exception for zero step")
        void shouldThrowForZeroStep() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.seqBy(0, 10, 0));
            assertEquals("step must be non-zero", exception.getMessage());
        }

        @Test
        @DisplayName("generates sequence with fractional step")
        void shouldGenerateWithFractionalStep() {
            var result = Sequence.seqBy(0, 1, 0.25).toList();
            assertEquals(List.of(0.0, 0.25, 0.5, 0.75, 1.0), result);
        }
    }

    @Nested
    @DisplayName("seqLength")
    class SeqLengthTests {

        @Test
        @DisplayName("generates sequence with specified length")
        void shouldGenerateWithSpecifiedLength() {
            var result = Sequence.seqLength(0, 10, 5).toList();
            assertEquals(5, result.size());
            assertEquals(0.0, result.get(0), 1e-10);
            assertEquals(2.5, result.get(1), 1e-10);
            assertEquals(5.0, result.get(2), 1e-10);
            assertEquals(7.5, result.get(3), 1e-10);
            assertEquals(10.0, result.get(4), 1e-10);
        }

        @Test
        @DisplayName("generates single element when length is 1")
        void shouldGenerateSingleElementWhenLengthIsOne() {
            var result = Sequence.seqLength(5, 10, 1).toList();
            assertEquals(List.of(5.0), result);
        }

        @Test
        @DisplayName("returns empty when length is 0")
        void shouldReturnEmptyWhenLengthIsZero() {
            var result = Sequence.seqLength(0, 10, 0).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("generates decreasing sequence")
        void shouldGenerateDecreasingSequence() {
            var result = Sequence.seqLength(10, 0, 3).toList();
            assertEquals(3, result.size());
            assertEquals(10.0, result.get(0), 1e-10);
            assertEquals(5.0, result.get(1), 1e-10);
            assertEquals(0.0, result.get(2), 1e-10);
        }

        @Test
        @DisplayName("throws exception for negative length")
        void shouldThrowForNegativeLength() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.seqLength(0, 10, -1));
            assertEquals("length must be non-negative, got: -1", exception.getMessage());
        }

        @Test
        @DisplayName("generates sequence with two elements")
        void shouldGenerateWithTwoElements() {
            var result = Sequence.seqLength(0, 100, 2).toList();
            assertEquals(List.of(0.0, 100.0), result);
        }
    }

    @Nested
    @DisplayName("seqLen")
    class SeqLenTests {

        @Test
        @DisplayName("generates sequence from 1 to n")
        void shouldGenerateSequenceFromOneToN() {
            var result = Sequence.seqLen(5).toList();
            assertEquals(List.of(1, 2, 3, 4, 5), result);
        }

        @Test
        @DisplayName("returns empty when n is 0")
        void shouldReturnEmptyWhenNIsZero() {
            var result = Sequence.seqLen(0).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("generates single element when n is 1")
        void shouldGenerateSingleElementWhenNIsOne() {
            var result = Sequence.seqLen(1).toList();
            assertEquals(List.of(1), result);
        }

        @Test
        @DisplayName("throws exception for negative n")
        void shouldThrowForNegativeN() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.seqLen(-5));
            assertEquals("n must be non-negative, got: -5", exception.getMessage());
        }

        @Test
        @DisplayName("generates large sequence")
        void shouldGenerateLargeSequence() {
            var result = Sequence.seqLen(100).toList();
            assertEquals(100, result.size());
            assertEquals(1, result.get(0));
            assertEquals(100, result.get(99));
        }
    }

    @Nested
    @DisplayName("seqAlong")
    class SeqAlongTests {

        @Test
        @DisplayName("generates sequence matching collection size")
        void shouldGenerateSequenceMatchingSize() {
            var collection = List.of("a", "b", "c", "d");
            var result = Sequence.seqAlong(collection).toList();
            assertEquals(List.of(1, 2, 3, 4), result);
        }

        @Test
        @DisplayName("returns empty for empty collection")
        void shouldReturnEmptyForEmptyCollection() {
            var result = Sequence.seqAlong(List.of()).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("works with different collection types")
        void shouldWorkWithDifferentTypes() {
            var numbers = List.of(10, 20, 30);
            var result = Sequence.seqAlong(numbers).toList();
            assertEquals(List.of(1, 2, 3), result);
        }

        @Test
        @DisplayName("throws exception for null collection")
        void shouldThrowForNullCollection() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.seqAlong(null));
            assertEquals("collection must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("repTimes (single value)")
    class RepTimesSingleValueTests {

        @Test
        @DisplayName("repeats value n times")
        void shouldRepeatValueNTimes() {
            var result = Sequence.repTimes("x", 5).toList();
            assertEquals(List.of("x", "x", "x", "x", "x"), result);
        }

        @Test
        @DisplayName("returns empty when times is 0")
        void shouldReturnEmptyWhenTimesIsZero() {
            var result = Sequence.repTimes(42, 0).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("throws exception for negative times")
        void shouldThrowForNegativeTimes() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.repTimes("x", -1));
            assertEquals("times must be non-negative, got: -1", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("repEach")
    class RepEachTests {

        @Test
        @DisplayName("repeats each element n times")
        void shouldRepeatEachElementNTimes() {
            var result = Sequence.repEach(List.of(1, 2, 3), 2).toList();
            assertEquals(List.of(1, 1, 2, 2, 3, 3), result);
        }

        @Test
        @DisplayName("returns empty when collection is empty")
        void shouldReturnEmptyWhenCollectionIsEmpty() {
            var result = Sequence.repEach(List.of(), 5).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty when each is 0")
        void shouldReturnEmptyWhenEachIsZero() {
            var result = Sequence.repEach(List.of(1, 2, 3), 0).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("repeats each element once")
        void shouldRepeatEachElementOnce() {
            var result = Sequence.repEach(List.of("a", "b", "c"), 1).toList();
            assertEquals(List.of("a", "b", "c"), result);
        }

        @Test
        @DisplayName("throws exception for negative each")
        void shouldThrowForNegativeEach() {
            var list = List.of(1, 2);
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.repEach(list, -1));
            assertEquals("each must be non-negative, got: -1", exception.getMessage());
        }

        @Test
        @DisplayName("throws exception for null collection")
        void shouldThrowForNullCollection() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.repEach(null, 3));
            assertEquals("collection must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("repTimes (collection)")
    class RepTimesCollectionTests {

        @Test
        @DisplayName("repeats entire collection n times")
        void shouldRepeatEntireCollectionNTimes() {
            var result = Sequence.repTimes(List.of(1, 2, 3), 2).toList();
            assertEquals(List.of(1, 2, 3, 1, 2, 3), result);
        }

        @Test
        @DisplayName("returns empty when collection is empty")
        void shouldReturnEmptyForEmptyCollection() {
            var result = Sequence.repTimes(List.of(), 5).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty when times is 0")
        void shouldReturnEmptyWhenTimesIsZero() {
            var result = Sequence.repTimes(List.of(1, 2, 3), 0).toList();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("repeats collection once")
        void shouldRepeatCollectionOnce() {
            var result = Sequence.repTimes(List.of("a", "b"), 1).toList();
            assertEquals(List.of("a", "b"), result);
        }

        @Test
        @DisplayName("throws exception for negative times")
        void shouldThrowForNegativeTimes() {
            var list = List.of(1, 2);
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.repTimes(list, -1));
            assertEquals("times must be non-negative, got: -1", exception.getMessage());
        }

        @Test
        @DisplayName("throws exception for null collection")
        void shouldThrowForNullCollection() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.repTimes((List<Integer>) null, 3));
            assertEquals("collection must not be null", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("concat")
    class ConcatTests {

        @Test
        @DisplayName("concatenates multiple collections")
        void shouldConcatenateMultipleCollections() {
            var result = Sequence.concat(
                    List.of(1, 2),
                    List.of(3, 4),
                    List.of(5, 6));
            assertEquals(List.of(1, 2, 3, 4, 5, 6), result);
        }

        @Test
        @DisplayName("concatenates single collection")
        void shouldConcatenateSingleCollection() {
            var result = Sequence.concat(List.of(1, 2, 3));
            assertEquals(List.of(1, 2, 3), result);
        }

        @Test
        @DisplayName("returns empty when no collections provided")
        void shouldReturnEmptyWhenNoCollections() {
            var result = Sequence.concat();
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("handles empty collections")
        void shouldHandleEmptyCollections() {
            var result = Sequence.concat(
                    List.of(1, 2),
                    List.of(),
                    List.of(3, 4));
            assertEquals(List.of(1, 2, 3, 4), result);
        }

        @Test
        @DisplayName("concatenates different types of collections")
        void shouldConcatenateDifferentCollectionTypes() {
            var result = Sequence.concat(
                    List.of("a", "b"),
                    java.util.Set.of("c", "d"));
            assertEquals(4, result.size());
            assertTrue(result.containsAll(List.of("a", "b", "c", "d")));
        }

        @Test
        @DisplayName("throws exception for null lists parameter")
        void shouldThrowForNullListsParameter() {
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> Sequence.concat((List<Integer>[]) null));
            assertEquals("lists must not be null", exception.getMessage());
        }

        @Test
        @DisplayName("handles null elements in varargs")
        void shouldHandleNullElementsInVarargs() {
            var result = Sequence.concat(
                    List.of(1, 2),
                    null,
                    List.of(3, 4));
            assertEquals(List.of(1, 2, 3, 4), result);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("combines range with repEach")
        void shouldCombineRangeWithRepEach() {
            var range = Sequence.range(1, 3).toList();
            var result = Sequence.repEach(range, 2).toList();
            assertEquals(List.of(1, 1, 2, 2, 3, 3), result);
        }

        @Test
        @DisplayName("combines seqLen with concat")
        void shouldCombineSeqLenWithConcat() {
            var seq1 = Sequence.seqLen(3).toList();
            var seq2 = Sequence.seqLen(2).toList();
            var result = Sequence.concat(seq1, seq2);
            assertEquals(List.of(1, 2, 3, 1, 2), result);
        }

        @Test
        @DisplayName("uses seqAlong with range-generated collection")
        void shouldUseSeqAlongWithRange() {
            var range = Sequence.range(10, 13).toList();
            var indices = Sequence.seqAlong(range).toList();
            assertEquals(List.of(1, 2, 3, 4), indices);
        }
    }
}
