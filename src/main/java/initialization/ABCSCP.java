package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static main.java.config.Parameters.RC_SIZE;
import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class ABCSCP {

    private final CommonUtils cUtils;

    public ABCSCP(AbcVars v) {
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet xj = new BitSet(COLUMNS);
        int[] u = new int[ROWS];

        generateSolution(xj, u);
        removeRedundantColumns(xj, u);

        return xj;
    }

    private void generateSolution(BitSet xj, int[] u) {
        IntStream.range(0, ROWS)
                .boxed()
                .forEach(i -> {
                    BitSet ai = getColumnsCoveringRow(i);
                    int randomRC = cUtils.randomNumber(RC_SIZE);
                    int j = ai.stream().boxed().toList().get(randomRC);

                    if (!xj.get(j)) {
                        xj.set(j);
                        BitSet bj = getRowsCoveredByColumn(j);
                        bj.stream().boxed().forEach(idx -> u[idx]++);
                    }
                });
    }

    private void removeRedundantColumns(BitSet xj, int[] u) {
        int numColumns = xj.cardinality() + 1;
        IntStream.range(1, numColumns)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(t -> {
                    int randomNum = cUtils.randomNumber(t);
                    int j = xj.stream().boxed().toList().get(randomNum);
                    BitSet bj = getRowsCoveredByColumn(j);

                    List<Integer> rowsCoveredByOneColumn =
                            bj.stream().boxed().filter(i -> u[i] < 2).toList();

                    if (rowsCoveredByOneColumn.isEmpty()) {
                        xj.clear(j);
                        bj.stream().boxed().forEach(idx -> u[idx]--);
                    }
                });
    }

}