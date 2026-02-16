package com._4meonweb.probability.sim;

import com._4meonweb.probability.Sampler;

import java.util.concurrent.atomic.AtomicInteger;
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
        AtomicInteger index = new AtomicInteger(0);
        return sampler.permutation(n)
                .anyMatch(value -> value == index.getAndIncrement());
    }
}