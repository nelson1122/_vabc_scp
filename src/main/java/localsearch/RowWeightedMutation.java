package main.java.localsearch;

import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.getCOLUMNINTS;
import static main.java.variables.ScpVars.getCOLUMNS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getROWS;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class RowWeightedMutation {
    private final CommonUtils cUtils;
    private int[] weights;
    private double[] priority;
    private double[] scores;
    private int[] timestamp = new int[COLUMNS];

    public RowWeightedMutation(AbcVars vr) {
        this.cUtils = new CommonUtils(vr);
        this.weights = new int[getROWS()];
        this.priority = new double[getCOLUMNS()];
        this.scores = new double[getCOLUMNS()];

        Arrays.fill(weights, 1);
    }

    public BitSet applyLocalSearch(BitSet fs) {
        return applyRowWeightMutationLocalSearch(fs);
    }

    private BitSet applyRowWeightMutationLocalSearch(BitSet fs) {
        BitSet fsMutation = (BitSet) fs.clone();
        BitSet uncoveredRows = cUtils.findUncoveredRows(fsMutation);

        calculateInitialPriority();
        calculateInitialScore(fsMutation);

        boolean improved = true;
        int colDrop1 = 0;
        int colDrop2 = 0;

        while (improved) {
            improved = false;

            while (uncoveredRows.isEmpty()) {
                colDrop1 = findColumnToDrop(fsMutation);
                updateSolutionDrop(colDrop1, fsMutation);
                uncoveredRows = cUtils.findUncoveredRows(fsMutation);
                updateScore(colDrop1, uncoveredRows);
            }

            colDrop2 = findColumnToDrop(fsMutation);
            updateSolutionDrop(colDrop2, fsMutation);
            uncoveredRows = cUtils.findUncoveredRows(fsMutation);
            updateScore(colDrop2, uncoveredRows);

            List<Integer> colsAdded = new ArrayList<>();
            while (!uncoveredRows.isEmpty()) {
                int colAdd = findColumnToAdd(uncoveredRows);
                colsAdded.add(colAdd);
                updateSolutionAdd(colAdd, fsMutation);
                uncoveredRows = cUtils.findUncoveredRows(fsMutation);
                updateScoreColumnsInSolution(fsMutation, colAdd);
                updateRowWeights(uncoveredRows);
                updateScoreColumnsNotInSolution(fsMutation, colAdd);
            }
            if (fitnessImproved(fs, fsMutation)) {
                fs = (BitSet) fsMutation.clone();
                improved = true;
            }
        }

        return fs;
    }

    private int findColumnToDrop(BitSet xj) {
        int colDrop;
        var columns = xj.stream().boxed().toList();

        double maxScore = scores[columns.get(0)];
        for (Integer j : columns) {
            if (scores[j] > maxScore) {
                maxScore = scores[j];
            }
        }

        List<Integer> colsToDrop = new ArrayList<>();
        for (Integer j : xj.stream().boxed().toList()) {
            if (scores[j] == maxScore) {
                colsToDrop.add(j);
            }
        }

        colDrop = colsToDrop.get(0);
        if (colsToDrop.size() > 1) {
            int maxTimes = timestamp[colDrop];
            for (Integer j : colsToDrop) {
                if (timestamp[j] > maxTimes) {
                    maxTimes = timestamp[j];
                    colDrop = j;
                }
            }
        }

        return colDrop;
    }

    private int findColumnToAdd(BitSet uncoveredRows) {
        int colAdd;
        var uRowList = uncoveredRows.stream().boxed().toList();
        int randRow = cUtils.randomNumber(uRowList.size());
        var row = uRowList.get(randRow);
        var cols = getColumnsCoveringRow(row).stream().boxed().toList();

        double maxScore = scores[cols.get(0)];
        for (Integer j : cols) {
            if (scores[j] > maxScore) {
                maxScore = scores[j];
            }
        }

        List<Integer> colsToAdd = new ArrayList<>();
        for (Integer j : cols) {
            if (scores[j] == maxScore) {
                colsToAdd.add(j);
            }
        }

        colAdd = colsToAdd.get(0);
        if (colsToAdd.size() > 1) {
            int maxTimes = timestamp[colAdd];
            for (Integer j : colsToAdd) {
                if (timestamp[j] > maxTimes) {
                    maxTimes = timestamp[j];
                    colAdd = j;
                }
            }
        }
        return colAdd;
    }

    private void updateSolutionAdd(int columnIndex, BitSet xj) {
        xj.set(columnIndex);
        timestamp[columnIndex]++;
        scores[columnIndex] = (-1) * scores[columnIndex];
    }

    private void updateSolutionDrop(int columnIndex, BitSet xj) {
        xj.clear(columnIndex);
        timestamp[columnIndex]++;
    }

    private void calculateInitialPriority() {
        for (Integer j : getCOLUMNINTS()) {

            BitSet Bj = getRowsCoveredByColumn(j);
            double sum = 0.0;

            for (Integer bj : Bj.stream().boxed().toList()) {
                sum += ((double) weights[bj] / getCost(j));
            }
            this.priority[j] = sum;
        }
    }

    private void calculateInitialScore(BitSet xj) {
        getCOLUMNINTS()
                .forEach(j -> {
                    double score;
                    if (xj.get(j)) {
                        score = (-1) * priority[j];
                    } else {
                        score = priority[j];
                    }
                    scores[j] = score;
                });
    }

    private void updateScore(int columnIndex, BitSet uncoveredRows) {
        BitSet rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        rowsCoveredByColumn.and(uncoveredRows);
        double score =
                rowsCoveredByColumn.stream()
                        .boxed()
                        .mapToDouble(j -> (double) weights[j] / getCost(columnIndex))
                        .sum();
        scores[columnIndex] = score;
    }

    private void updateScoreColumnsInSolution(BitSet xj, int columnIndex) {
        BitSet xjc = (BitSet) xj.clone();
        BitSet Bj = getRowsCoveredByColumn(columnIndex);

        xjc.clear(columnIndex);
        for (Integer h : xjc.stream().boxed().toList()) {
            BitSet Bh = getRowsCoveredByColumn(h);
            Bh.and(Bj);

            double sum = 0.0;
            for (int i : Bh.stream().boxed().toList()) {
                sum += ((double) weights[i] / getCost(h));
            }

            double score = scores[h] + sum;

            scores[h] = score;
        }
    }

    private void updateRowWeights(BitSet uncoveredRows) {
        uncoveredRows.stream().boxed().forEach(i -> weights[i]++);
    }

    private void updateScoreColumnsNotInSolution(BitSet xj, int j) {
        BitSet Bj = getRowsCoveredByColumn(j);

        for (int h = 0; h < getCOLUMNS(); h++) {
            if (!xj.get(h)) {
                BitSet Bh = getRowsCoveredByColumn(h);
                Bh.and(Bj);

                double sum = 0.0;
                for (int i : Bh.stream().boxed().toList()) {
                    sum += ((double) weights[i] / getCost(h));
                }

                double score = scores[h] - sum;

                scores[h] = score;
            }
        }
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
