package main.java.localsearch;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

import static main.java.config.ParamsConfig.FOOD_NUMBER;
import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getCOLUMNINTS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class RowWeightedMutation {

    private final AbcVars vr;
    private final CommonUtils cUtils;
    private int[] w;
    private double[] p;
    private double[] s;
    private int[] timestamp = new int[COLUMNS];

    public RowWeightedMutation(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
        this.w = new int[ROWS];
        this.p = new double[COLUMNS];
        this.s = new double[COLUMNS];
    }

    public BitSet applyLocalSearch(BitSet fs) {
        Arrays.fill(w, 1);
        Arrays.fill(p, 0.0);
        Arrays.fill(s, 0.0);

        fs = applyRowWeightMutationLocalSearch(fs);

        return fs;
    }

    private BitSet applyRowWeightMutationLocalSearch(BitSet fs) {
        BitSet fsMutation = (BitSet) fs.clone();
        BitSet uncoveredRows = cUtils.findUncoveredRows(fsMutation);

        calculateInitialPriority();
        calculateInitialScore(fsMutation);

        int colDrop = 0;
        int colAdd = 0;

        boolean improved = true;

        while (improved) {
            improved = false;

            while (uncoveredRows.isEmpty()) {
                double maxScore = fsMutation.stream()
                        .boxed()
                        .mapToDouble(j -> s[j])
                        .min()
                        .getAsDouble();

                colDrop = fsMutation.stream()
                        .filter(j -> s[j] == maxScore)
                        .boxed()
                        .map(j -> new Tuple2<>(j, timestamp[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionDrop(colDrop, fsMutation);
                uncoveredRows = cUtils.findUncoveredRows(fsMutation);
                updateScore(colDrop, uncoveredRows);
            }

            double maxScore1 = fsMutation.stream()
                    .boxed()
                    .mapToDouble(j -> s[j])
                    .max()
                    .getAsDouble();

            int colDrop1 = fsMutation.stream()
                    .filter(j -> s[j] == maxScore1)
                    .boxed()
                    .map(j -> new Tuple2<>(j, timestamp[j]))
                    .sorted(Comparator.comparing(Tuple2::getT2))
                    .map(Tuple2::getT1)
                    .toList().get(0);

            updateSolutionDrop(colDrop1, fsMutation);
            uncoveredRows = cUtils.findUncoveredRows(fsMutation);
            updateScore(colDrop1, uncoveredRows);

            while (!uncoveredRows.isEmpty()) {
                int randomRow = cUtils.randomNumber(uncoveredRows.cardinality());
                int uncoveredRow = uncoveredRows.stream().boxed().toList().get(randomRow);
                BitSet cols = getColumnsCoveringRow(uncoveredRow);

                double maxScore = cols.stream()
                        .mapToDouble(j -> s[j])
                        .max()
                        .getAsDouble();

                colAdd = cols.stream()
                        .boxed()
                        .filter(j -> s[j] == maxScore)
                        .map(j -> new Tuple2<>(j, timestamp[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionAdd(colAdd, fsMutation);
                uncoveredRows = cUtils.findUncoveredRows(fsMutation);
                updateScoreColumnsInSolution(fsMutation, colAdd);
                updateRowWeights(uncoveredRows);
                updateScoreColumnsNotInSolution(fsMutation, colAdd);
            }

            if (solutionImproved(fs, fsMutation)) {
                fs = (BitSet) fsMutation.clone();
                improved = true;
            }
        }

        return fs;
    }

    private void updateSolutionAdd(int columnIndex, BitSet xj) {
        xj.set(columnIndex);
        timestamp[columnIndex]++;
        s[columnIndex] = (-1) * s[columnIndex];
    }

    private void updateSolutionDrop(int columnIndex, BitSet xj) {
        xj.clear(columnIndex);
        timestamp[columnIndex]++;
    }

    private void calculateInitialPriority() {
        getCOLUMNINTS()
                .forEach(j -> {
                    BitSet Bj = getRowsCoveredByColumn(j);
                    double priority = (double) Bj.cardinality() / getCost(j);
                    p[j] = priority;
                });
    }

    private void calculateInitialScore(BitSet xj) {
        getCOLUMNINTS()
                .forEach(j -> {
                    double score;
                    if (xj.get(j)) {
                        score = (-1) * p[j];
                    } else {
                        score = p[j];
                    }
                    s[j] = score;
                });
    }

    private void updateScore(int columnIndex, BitSet uncoveredRows) {
        BitSet rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        rowsCoveredByColumn.and(uncoveredRows);
        double score =
                rowsCoveredByColumn.stream()
                        .boxed()
                        .mapToDouble(j -> (double) w[j] / getCost(columnIndex))
                        .sum();
        s[columnIndex] = score;
    }

    private void updateScoreColumnsInSolution(BitSet xj, int columnIndex) {
        BitSet xjc = (BitSet) xj.clone();
        BitSet Bj = getRowsCoveredByColumn(columnIndex);

        xjc.stream()
                .boxed()
                .filter(j -> !j.equals(columnIndex))
                .forEach(h -> {
                    BitSet Bh = getRowsCoveredByColumn(h);
                    Bh.and(Bj);

                    double sum = 0.0;
                    for (int i : Bh.stream().boxed().toList()) {
                        sum += ((double) w[i] / getCost(h));
                    }

                    double score = s[h] + sum;

                    s[h] = score;
                });
    }

    private void updateRowWeights(BitSet uncoveredRows) {
        uncoveredRows.stream().boxed().forEach(i -> w[i]++);
    }

    private void updateScoreColumnsNotInSolution(BitSet xj, int columnIndex) {
        BitSet Bj = getRowsCoveredByColumn(columnIndex);

        getCOLUMNINTS()
                .stream()
                .filter(j -> !xj.get(j))
                .forEach(h -> {
                    BitSet Bh = getRowsCoveredByColumn(h);
                    Bh.and(Bj);

                    double sum = 0.0;
                    for (int i : Bh.stream().boxed().toList()) {
                        sum += ((double) w[i] / getCost(h));
                    }

                    double score = s[h] - sum;

                    s[h] = score;
                });
    }


    private boolean solutionImproved(BitSet currXj, BitSet newXj) {
        int currFiness = cUtils.calculateFitnessOne(currXj);
        int newFitness = cUtils.calculateFitnessOne(newXj);

//        if (currFiness > newFitness) {
//            System.out.println("Fitness improved => [" + currFiness + ", " + newFitness + "]");
//            if (newFitness < 156) {
//                System.out.println("BEST REACHED");
//            }
//        }

        return currFiness > newFitness;
    }
}
