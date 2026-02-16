package com._4meonweb.probability.sim;

import java.util.logging.Logger;

public class SimMain {

    private static final Logger LOG = Logger.getLogger(SimMain.class.getName());

    public static void main(String[] args) {
        Simulation sim = new MatchingProblem();
        double result = sim.run();

        LOG.info(() -> "Estimated probability: " + result);
        LOG.info(() -> "Expected (1 - 1/e): " + (1 - Math.exp(-1)));
    }
}