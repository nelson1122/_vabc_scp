package main.java.utils;

import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;

import static main.java.config.ParamsConfig.RC_SIZE;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRatioCostRowsCovered;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class RepairUtils {

    private final CommonUtils cUtils;

    public RepairUtils(AbcVars vr) {
        this.cUtils = new CommonUtils(vr);
    }

    public int getColumnMinRatio(BitSet uncoveredRows, int i) {
        BitSet ai = getColumnsCoveringRow(i);
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

    public void removeRedundantColumnsStream(BitSet cfs) {
        BitSet cfsCopy = (BitSet) cfs.clone();
        cfsCopy.stream()
                .boxed()
                .map(j -> new Tuple(j, getRatioCostRowsCovered(j)))
                .sorted(Collections.reverseOrder(Comparator.comparing(Tuple::getT2).thenComparing(Tuple::getT1)))
                .map(Tuple::getT1)
                .forEach(j -> {
                    cfs.clear(j);
                    BitSet uncoveredRows = cUtils.findUncoveredRows(cfs);
                    if (!uncoveredRows.isEmpty()) {
                        cfs.set(j);
                    }
                });
    }


    public int selectRandomColumnFromRCL(int i) {
        int randomRC = cUtils.randomNumber(RC_SIZE);
        BitSet ai = getColumnsCoveringRow(i);
        return ai.stream().boxed().toList().get(randomRC);
    }
}
