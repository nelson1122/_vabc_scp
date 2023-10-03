package main.java;

import main.java.localsearch.ABCSCP;
import main.java.localsearch.RowWeightedMutation;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class LocalSearch {

    private final AbcVars vr;
    private final ABCSCP abcscp;

    private final RowWeightedMutation rowWeightedMutation;

    public LocalSearch(AbcVars vr) {
        this.vr = vr;
        this.abcscp = new ABCSCP();
        this.rowWeightedMutation = new RowWeightedMutation(vr);
    }

    public BitSet apply(BitSet cfs) {
        return switch (vr.getAlgorithmVariant()) {
            case 0 -> this.abcscp.applyLocalSearch(cfs);
            case 1, 2, 3 -> this.rowWeightedMutation.applyLocalSearch(cfs);
            default -> throw new IllegalArgumentException("Local Search method is not valid");
        };
    }
}
