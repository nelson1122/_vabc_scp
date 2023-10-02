package main.java;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static main.java.config.Parameters.Pa;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


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

            double r = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
            double rNum = Math.round(r * 1000) / 1000.0;

            if (rNum <= Pa) {
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
                .map(j -> {
                    BitSet rowsCovered = getRowsCoveredByColumn(j);
                    double ratio = (double) getCost(j) / rowsCovered.cardinality();
                    return new Tuple2<>(j, cUtils.roundDouble(ratio));
                })
                .sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                .map(Tuple2::getT1)
                .forEach(columnIndex -> {
                    cfs.clear(columnIndex);
                    BitSet uncoveredRows = cUtils.findUncoveredRows(cfs);
                    if (!uncoveredRows.isEmpty()) {
                        cfs.set(columnIndex);
                    }
                });
    }
}
