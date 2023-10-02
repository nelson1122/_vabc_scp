package main.java;

import main.java.localsearch.ABCSCP;

import java.util.BitSet;

public class LocalSearch {
    private final ABCSCP abcscp;

    public LocalSearch() {
        this.abcscp = new ABCSCP();
    }

    public BitSet apply(BitSet cfs) {
        return this.abcscp.applyLocalSearch(cfs);
    }
}
