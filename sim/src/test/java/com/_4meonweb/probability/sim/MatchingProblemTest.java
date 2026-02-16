package com._4meonweb.probability.sim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingProblemTest {

    @Test
    void probabilityIsCloseToOneMinusE() {
        MatchingProblem sim = new MatchingProblem();
        double probability = sim.run();

        double expected = 1 - Math.exp(-1);
        assertTrue(Math.abs(probability - expected) < 0.02);
    }
}
