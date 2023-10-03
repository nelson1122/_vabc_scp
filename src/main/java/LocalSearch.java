package main.java;

import main.java.localsearch.ABCSCP;
import main.java.localsearch.RowWeightedMutation;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class LocalSearch {
    private final ABCSCP abcscp;

    private final RowWeightedMutation rowWeightedMutation;

    public LocalSearch(AbcVars vr) {
        this.abcscp = new ABCSCP();
        this.rowWeightedMutation = new RowWeightedMutation(vr);
    }

    public BitSet apply(BitSet cfs) {
        // return this.abcscp.applyLocalSearch(cfs);
        return this.rowWeightedMutation.applyLocalSearch(cfs);
    }
}
