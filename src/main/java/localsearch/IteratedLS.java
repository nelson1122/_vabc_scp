package main.java.localsearch;

import main.java.Repair;
import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;
import main.java.variables.ScpVars;

import java.util.*;
import java.util.stream.Collectors;

import static main.java.variables.ScpVars.*;

public class IteratedLS {
    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final Repair repair;
    private static final double Pa = 0.5;
    private final double[] penalties;
    private static final double ALPHA = 0.05d;
    private static final double BETHA = 0.01d;
    private static final double MIN_PENALTY = 0.5;
    private static final double MAX_PENALTY = 1.5;

    public IteratedLS(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
        this.repair = new Repair(vr);
        this.penalties = new double[COLUMNS];
    }

    public BitSet applyLocalSearch(BitSet fs) {
        Arrays.fill(this.penalties, 1);

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
        var L1 = new BitSet();
        var L2 = new BitSet();

        List<Integer> columns = fs.stream().boxed().toList();

        int n = fs.cardinality();
        int nCols = n > 35 ? 20 : 6;


        List<Integer> droppedCols = new ArrayList<>();
        while (nCols > 0) {
            int index = cUtils.randomNumber(n);
            int j = columns.get(index);

            if (!droppedCols.contains(j)) {
                double r = vr.getNextDouble();
                if (r < Pa) {
                    L1.set(j);
                } else {
                    L2.set(j);
                }
                droppedCols.add(j);
                nCols--;
            }
        }

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
            increasePenalty(j);
            mapList.removeIf(row -> row.get(j));
        }
        decreasePenalties(fs);
        repair.removeRedundantColumnsStream(fs);
    }

    private void increasePenalty(int j) {
        double pj = penalties[j] + ALPHA;
        if (pj > MAX_PENALTY) pj = MAX_PENALTY;
        penalties[j] = pj;
    }

    private void decreasePenalties(BitSet fs) {
        for (int j = 0; j < getCOLUMNS(); j++) {
            if (!fs.get(j)) {
                double pj = penalties[j] - BETHA;
                if (pj < MIN_PENALTY) pj = MIN_PENALTY;
                penalties[j] = pj;
            }
        }
    }

    private double calculateFunction(BitSet fs, int j) {
        BitSet uncoveredRows = cUtils.findUncoveredRows(fs);
        BitSet rowsCoveredByColumn = getRowsCoveredByColumn(j);
        uncoveredRows.and(rowsCoveredByColumn);
        return ((double) getCost(j) * penalties[j]) / uncoveredRows.cardinality();
    }

    private boolean fitnessImproved(BitSet cfs, BitSet newfs) {
        int currFiness = cUtils.calculateFitnessOne(cfs);
        int newFitness = cUtils.calculateFitnessOne(newfs);
        if (currFiness == newFitness) {
            int currFitnessTwo = cUtils.calculateFitnessTwo(cfs);
            int newFitnessTwo = cUtils.calculateFitnessTwo(newfs);
            return currFitnessTwo > newFitnessTwo;
        }
        return currFiness > newFitness;
    }

}
