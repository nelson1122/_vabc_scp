package main.java;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static main.java.config.ParamsConfig.Pa;
import static main.java.config.ParamsConfig.RC_SIZE;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRatioCostRowsCovered;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class Repair {
    private final AbcVars vr;
    private final CommonUtils cUtils;

    public Repair(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
    }

    public void applyRepairSolution(BitSet cfs, BitSet uncoveredRows) {
        makeSolutionFeasible(cfs, uncoveredRows);
        removeRedundantColumnsStream(cfs);
    }

    public void makeSolutionFeasible(BitSet cfs, BitSet uncoveredRows) {
        while (!uncoveredRows.isEmpty()) {
            List<Integer> uncoveredRowsList = uncoveredRows.stream().boxed().toList();
            int row = uncoveredRowsList.get(0);
            int j;

            double r = vr.getNextDouble();

            if (r <= Pa) {
                j = getColumnMinRatio(uncoveredRows, row);
            } else {
                j = selectRandomColumnFromRCL(row);
            }

            cfs.set(j);
            uncoveredRows = cUtils.findUncoveredRows(cfs);
        }
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

    private int getColumnMinRatio(BitSet uncoveredRows, int i) {
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

    private int selectRandomColumnFromRCL(int i) {
        int randomRC = cUtils.randomNumber(RC_SIZE);
        BitSet ai = getColumnsCoveringRow(i);
        return ai.stream().boxed().toList().get(randomRC);
    }
}
