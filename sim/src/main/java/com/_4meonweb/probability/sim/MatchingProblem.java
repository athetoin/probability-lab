package com._4meonweb.probability.sim;

import com._4meonweb.probability.Sampler;

import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public class MatchingProblem implements Simulation {

    @Override
    public double run() {
        int n = 100; // number of cards
        int runs = 10_000; // number of simulations

        Sampler sampler = new Sampler(RandomGenerator.getDefault());

        long countWithMatch = IntStream.range(0, runs)
                .filter(i -> hasMatch(n, sampler))
                .count();

        return (double) countWithMatch / runs;
    }

    private static boolean hasMatch(int n, Sampler sampler) {
        // Your library already has R-like sampling: sampleIntRange(n)
        int[] perm = sampler.permutation(n)
                .mapToInt(Integer::intValue)
                .toArray();

        // Check if any position matches
        return IntStream.range(0, n)
                .anyMatch(i -> perm[i] == i);
    }
}