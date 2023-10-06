package main.java;

import main.java.initialization.ABCSCP;
import main.java.initialization.RandomHeuristic;
import main.java.initialization.RandomMethod;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class Initialization {
    private AbcVars vr;
    private final ABCSCP abcscp;
    private final RandomMethod randomMethod;

    private final RandomHeuristic randomHeuristic;

    public Initialization(AbcVars vr) {
        this.vr = vr;
        this.abcscp = new ABCSCP(vr);
        this.randomMethod = new RandomMethod(vr);
        this.randomHeuristic = new RandomHeuristic(vr);
    }

    public BitSet createSolution() {
        return switch (vr.getInitializeMethod()) {
            case 0 -> this.abcscp.createSolution();
            case 1 -> this.randomMethod.createSolution();
            case 2 -> this.randomHeuristic.createSolution();
            case 3 -> new BitSet(); // change
            default -> throw new IllegalArgumentException("Init Method is not valid");
        };
    }
}
