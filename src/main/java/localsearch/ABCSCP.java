package main.java.localsearch;

import main.java.variables.ScpVars;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static main.java.variables.ScpVars.*;

public class ABCSCP {
    public ABCSCP() {
    }

    public BitSet applyLocalSearch(BitSet cfs) {
        int[] u = new int[getROWS()];

        // 1- Computing for each row i the number of columns ui in the current solution covering it.
        cfs.stream()
                .boxed()
                .map(ScpVars::getRowsCoveredByColumn)
                .map(x -> x.stream().boxed().toList())
                .flatMap(Collection::stream)
                .forEach(i -> u[i]++);

        AtomicBoolean improved = new AtomicBoolean(true);

        while (improved.get()) {
            improved.set(false);

            cfs.stream()
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .forEach(j -> {
                        List<Integer> rowsCoveredByOneColumn = new ArrayList<>();
                        BitSet bj = getRowsCoveredByColumn(j);
                        for (int rowIndex : bj.stream().boxed().toList()) {
                            if (u[rowIndex] == 1) {
                                rowsCoveredByOneColumn.add(rowIndex);
                            }
                        }
                        int pj = rowsCoveredByOneColumn.size();
                        if (pj == 0) {
                            cfs.clear(j);
                            updateUi(u, j, false);
                            improved.set(true);
                        }
                        if (pj == 1) {
                            int row = rowsCoveredByOneColumn.get(0);
                            int minCostColumn = findMinCostColumn(row); // Implement this function
                            if (minCostColumn != j) {
                                cfs.clear(j);
                                cfs.set(minCostColumn);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn, true);
                                improved.set(true);
                            }
                        }
                        if (pj == 2) {
                            int row1 = rowsCoveredByOneColumn.get(0);
                            int row2 = rowsCoveredByOneColumn.get(1);

                            int minCostColumn1 = findMinCostColumn(row1); // Implement this function
                            int minCostColumn2 = findMinCostColumn(row2); // Implement this function

                            int sumCosts = calculateColumnCost(minCostColumn1) + calculateColumnCost(minCostColumn2);

                            if (minCostColumn1 != minCostColumn2 && sumCosts <= calculateColumnCost(j)) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                cfs.set(minCostColumn2);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                                updateUi(u, minCostColumn2, true);
                                improved.set(true);
                            } else if (minCostColumn1 == minCostColumn2 && minCostColumn1 != j) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                                improved.set(true);
                            }
                        }
                        if (pj == 3) {
                            int row1 = rowsCoveredByOneColumn.get(0);
                            int row2 = rowsCoveredByOneColumn.get(1);
                            int row3 = rowsCoveredByOneColumn.get(2);

                            int minCostColumn1 = findMinCostColumn(row1); // Implement this function
                            int minCostColumn2 = findMinCostColumn(row2); // Implement this function
                            int minCostColumn3 = findMinCostColumn(row3); // Implement this function

                            int sumCosts = calculateColumnCost(minCostColumn1) + calculateColumnCost(minCostColumn2) + calculateColumnCost(minCostColumn3);

                            if (minCostColumn1 != minCostColumn2 && minCostColumn1 != minCostColumn3 && minCostColumn2 != minCostColumn3 && sumCosts <= calculateColumnCost(j)) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                cfs.set(minCostColumn2);
                                cfs.set(minCostColumn3);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                                updateUi(u, minCostColumn2, true);
                                updateUi(u, minCostColumn3, true);
                                improved.set(true);
                            } else if (minCostColumn1 == minCostColumn2 && minCostColumn2 == minCostColumn3 && minCostColumn1 != j) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                            } else if (minCostColumn1 == minCostColumn2 && minCostColumn1 != minCostColumn3 && sumCosts <= calculateColumnCost(j)) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                cfs.set(minCostColumn3);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                                updateUi(u, minCostColumn3, true);
                                improved.set(true);
                            } else if (minCostColumn1 == minCostColumn3 && minCostColumn1 != minCostColumn2 && sumCosts <= calculateColumnCost(j)) {
                                cfs.clear(j);
                                cfs.set(minCostColumn1);
                                cfs.set(minCostColumn2);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn1, true);
                                updateUi(u, minCostColumn2, true);
                                improved.set(true);
                            } else if (minCostColumn2 == minCostColumn3 && minCostColumn2 != minCostColumn1 && sumCosts <= calculateColumnCost(j)) {
                                cfs.clear(j);
                                cfs.set(minCostColumn2);
                                cfs.set(minCostColumn1);
                                updateUi(u, j, false);
                                updateUi(u, minCostColumn2, true);
                                updateUi(u, minCostColumn1, true);
                                improved.set(true);
                            }
                        }
                    });
        }
        return cfs;
    }


    private static int findMinCostColumn(int i) {
        // Implement your logic here
        List<Integer> ai = getColumnsCoveringRow(i).stream().boxed().toList();
        return ai.get(0); // Replace with actual column index
    }

    private static int calculateColumnCost(int j) {
        // Implement your logic here
        return getCost(j); // Replace with actual cost value
    }

    private static void updateUi(int[] ui, int j, boolean add) {
        for (int row : getRowsCoveredByColumn(j).stream().boxed().toList()) {
            if (add) {
                ui[row]++;
            } else {
                ui[row]--;
            }
        }
    }
}
