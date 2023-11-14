package main.java.localsearch;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;
import main.java.variables.ScpVars;

import java.util.*;
import java.util.stream.Collectors;

import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class IteratedLS {
    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final double Pa = 0.2;

    public IteratedLS(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
    }

    public BitSet applyLocalSearch(BitSet fs) {
        boolean improved = true;
        while (improved) {
            improved = false;
            List<BitSet> groupedLists = grouping(fs);
            Stack<BitSet> Q = new Stack<>();
            Q.push(groupedLists.get(0));
            Q.push(groupedLists.get(1));

            while (!Q.isEmpty()) {
                BitSet newfs = (BitSet) fs.clone();
                BitSet L = Q.pop();
                L.stream().forEach(newfs::clear);
                randomGreedy(newfs);
                if (fitnessImproved(fs, newfs)) {
                    fs = (BitSet) newfs.clone();
                    List<BitSet> newGroupedLists = grouping(newfs);
                    Q.push(newGroupedLists.get(0));
                    Q.push(newGroupedLists.get(1));
                    improved = true;
                }
            }
        }
        return fs;
    }

    private List<BitSet> grouping(BitSet fs) {
        BitSet L1 = new BitSet();
        BitSet L2 = new BitSet();

        fs.stream()
                .boxed()
                .forEach(j -> {
                    double r = vr.getNextDouble();
                    if (r < Pa) {
                        if (vr.getRANDOM().nextBoolean()) {
                            L1.set(j);
                        } else {
                            L2.set(j);
                        }
                    }
                });

        List<BitSet> groupedLists = new ArrayList<>();
        groupedLists.add(L1);
        groupedLists.add(L2);
        return groupedLists;
    }

    private void randomGreedy(BitSet fs) {
        BitSet uncoveredRows = cUtils.findUncoveredRows(fs);

        List<BitSet> mapList = uncoveredRows.stream().boxed()
                .map(ScpVars::getColumnsCoveringRow)
                .sorted(Comparator.comparing(BitSet::cardinality))
                .collect(Collectors.toList());

        while (!mapList.isEmpty()) {
            int j = mapList.get(0).stream()
                    .boxed()
                    .map(col -> new Tuple2<>(col, calculateFunction(fs, col)))
                    .sorted(Comparator.comparing(Tuple2::getT2))
                    .map(Tuple2::getT1)
                    .toList()
                    .get(0);
            fs.set(j);
            mapList.removeIf(row -> row.get(j));
        }
    }

    private double calculateFunction(BitSet fs, int j) {
        BitSet uncoveredRows = cUtils.findUncoveredRows(fs);
        BitSet rowsCoveredByColumn = getRowsCoveredByColumn(j);
        uncoveredRows.and(rowsCoveredByColumn);
        return (double) getCost(j) / uncoveredRows.cardinality();
    }

    private boolean fitnessImproved(BitSet cfs, BitSet newfs) {
        int currFiness = cUtils.calculateFitnessOne(cfs);
        int newFitness = cUtils.calculateFitnessOne(newfs);
        return currFiness > newFitness;
    }

}
