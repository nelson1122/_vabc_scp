package main.java;

import main.java.initialization.ABCSCP;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class Initialization {
    private final ABCSCP abcscp;

    public Initialization(AbcVars v) {
        this.abcscp = new ABCSCP(v);
    }

    public BitSet createSolution() {
        return this.abcscp.createSolution();
    }
}
