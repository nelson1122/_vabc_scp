package main.java.utils;

import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Comparator;

import static main.java.config.ParamsConfig.RC_SIZE;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class RepairUtils {

    private final CommonUtils cUtils;

    public RepairUtils(AbcVars vr) {
        this.cUtils = new CommonUtils(vr);
    }

    public int getColumnMinRatio(BitSet uncoveredRows, int rowIndex) {
        BitSet ai = getColumnsCoveringRow(rowIndex);
        return ai.stream()
                .boxed()
                .map(j -> {
                    BitSet bj = getRowsCoveredByColumn(j);
                    BitSet ur = (BitSet) uncoveredRows.clone();
                    ur.and(bj);
                    double ratio = (double) getCost(j) / ur.cardinality();
                    return new Tuple2<>(j, ratio);
                })
                .sorted(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .toList()
                .get(0);
    }


    public int selectRandomColumnFromRCL(int i) {
        int randomRC = cUtils.randomNumber(RC_SIZE);
        BitSet ai = getColumnsCoveringRow(i);
        return ai.stream().boxed().toList().get(randomRC);
    }
}
