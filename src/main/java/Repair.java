package main.java;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static main.java.config.ParamsConfig.Pa;
import static main.java.variables.ScpVars.getRatioCostRowsCovered;


public class Repair {
    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;

    public Repair(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
        this.rUtils = new RepairUtils(v);
    }

    public void applyRepairSolution(BitSet cfs, BitSet uncoveredRows) {
        makeSolutionFeasible(cfs, uncoveredRows);
        removeRedundantColumnsStream(cfs);
    }

    private void makeSolutionFeasible(BitSet cfs, BitSet uncoveredRows) {
        while (!uncoveredRows.isEmpty()) {
            List<Integer> uncoveredRowsList = uncoveredRows.stream().boxed().toList();
            int indexRowUncovered = uncoveredRowsList.get(0);
            int columnIndex;

            double r = vr.getNextDouble();
//            double rNum = cUtils.roundDouble(r);

            if (r < Pa) {
                columnIndex = rUtils.getColumnMinRatio(uncoveredRows, indexRowUncovered);
            } else {
                columnIndex = rUtils.selectRandomColumnFromRCL(indexRowUncovered);
            }

            cfs.set(columnIndex);
            uncoveredRows = cUtils.findUncoveredRows(cfs);
        }
    }

    private void removeRedundantColumnsStream(BitSet cfs) {
        BitSet cfsCopy = (BitSet) cfs.clone();
        cfsCopy.stream()
                .boxed()
                .map(j -> new Tuple(j, getRatioCostRowsCovered(j)))
                .sorted(Collections.reverseOrder(Comparator.comparing(Tuple::getT2)
                        .thenComparing(Tuple::getT1)))
                .map(Tuple::getT1)
                .forEach(columnIndex -> {
                    cfs.clear(columnIndex);
                    BitSet uncoveredRows = cUtils.findUncoveredRows(cfs);
                    if (!uncoveredRows.isEmpty()) {
                        cfs.set(columnIndex);
                    }
                });
    }
}
