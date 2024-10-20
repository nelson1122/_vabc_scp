package main.java;

import main.java.localsearch.ABCSCP;
import main.java.localsearch.IteratedLS;
import main.java.localsearch.RowWeightedMutation;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class LocalSearch {

    private final AbcVars vr;
    private final ABCSCP abcscp;
    private final RowWeightedMutation rowWeightedMutation;
    private final IteratedLS iteratedLS;

    public LocalSearch(AbcVars vr) {
        this.vr = vr;
        this.abcscp = new ABCSCP();
        this.rowWeightedMutation = new RowWeightedMutation(vr);
        this.iteratedLS = new IteratedLS(vr);
    }

    public BitSet apply(BitSet cfs) {
        return switch (vr.getLocalSearchMethod()) {
            case 0 -> cfs;
            case 1 -> this.abcscp.applyLocalSearch(cfs);
            case 2 -> this.rowWeightedMutation.applyLocalSearch(cfs);
            case 3 -> this.iteratedLS.applyLocalSearch(cfs);
            default -> throw new IllegalArgumentException("Local Search method is not valid");
        };
    }
}
