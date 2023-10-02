package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.getCOLUMNS;

public class RandomMethod {
    private final AbcVars vr;
    private final CommonUtils cUtils;
    private static final double RATIO = 0.5;

    public RandomMethod(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
    }

    public BitSet createSolution() {
        BitSet fs = new BitSet();
        fs.set(0, getCOLUMNS(), true);
        IntStream.range(0, getCOLUMNS())
                .boxed()
                .forEach(j -> {
                    double r = vr.getRANDOM().nextDouble();
                    double rNum = cUtils.roundDouble(r);

                    if (rNum < RATIO) {
                        fs.clear(j);
                    }

                });
        return fs;
    }
}
