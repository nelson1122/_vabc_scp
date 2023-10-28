package main.java.initialization;

import main.java.variables.AbcVars;

import java.util.BitSet;

import static main.java.variables.ScpVars.getCOLUMNINTS;

public class RandomMethod {
    private final AbcVars vr;
    private static final double RATIO = 0.5;

    public RandomMethod(AbcVars vr) {
        this.vr = vr;
    }

    public BitSet createSolution() {
        BitSet fs = new BitSet();
        getCOLUMNINTS()
                .forEach(j -> {
                    double r = vr.getRANDOM().nextDouble();
                    if (r < RATIO) {
                        fs.set(j);
                    }
                });
        return fs;
    }
}
