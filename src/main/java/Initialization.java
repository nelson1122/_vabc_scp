package main.java;

import main.java.initialization.ABCSCP;
import main.java.initialization.RandomHeuristic;
import main.java.initialization.RandomMethod;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class Initialization {
    private final ABCSCP abcscp;
    private final RandomMethod randomMethod;

    private final RandomHeuristic randomHeuristic;

    public Initialization(AbcVars vr) {
        this.abcscp = new ABCSCP(vr);
        this.randomMethod = new RandomMethod(vr);
        this.randomHeuristic = new RandomHeuristic(vr);
    }

    public BitSet createSolution() {
        // return this.abcscp.createSolution();
        // return this.randomMethod.createSolution();
        return this.randomHeuristic.createSolution();
    }
}
