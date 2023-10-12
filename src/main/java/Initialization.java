package main.java;

import main.java.initialization.ABCSCP;
import main.java.initialization.IterativeConstruction;
import main.java.initialization.RandomHeuristic;
import main.java.initialization.RandomMethod;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class Initialization {
    private final AbcVars vr;
    private final ABCSCP abcscp;
    private final RandomMethod randomMethod;
    private final RandomHeuristic randomHeuristic;
    private final IterativeConstruction iConstruction;

    public Initialization(AbcVars vr) {
        this.vr = vr;
        this.abcscp = new ABCSCP(vr);
        this.randomMethod = new RandomMethod(vr);
        this.randomHeuristic = new RandomHeuristic(vr);
        this.iConstruction = new IterativeConstruction(vr);
    }

    public BitSet createSolution() {
        return switch (vr.getInitializeMethod()) {
            case 0 -> this.abcscp.createSolution();
            case 1 -> this.randomMethod.createSolution();
            case 2 -> this.randomHeuristic.createSolution();
            case 3 -> this.iConstruction.createSolution(); // change
            default -> throw new IllegalArgumentException("Init Method is not valid");
        };
    }
}
