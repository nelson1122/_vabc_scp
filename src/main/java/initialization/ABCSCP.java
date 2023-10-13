package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static main.java.config.ParamsConfig.RC_SIZE;
import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class ABCSCP {

    private final CommonUtils cUtils;

    public ABCSCP(AbcVars v) {
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet fs = new BitSet(COLUMNS);
        int[] u = new int[ROWS];

        generateSolution(fs, u);
        removeRedundantColumns(fs, u);

        return fs;
    }

    private void generateSolution(BitSet fs, int[] u) {
        IntStream.range(0, ROWS)
                .boxed()
                .map(j -> new Tuple2<>(j, getCost(j)))
                .sorted(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .forEach(i -> {
                    BitSet ai = getColumnsCoveringRow(i);
                    int randomRC = cUtils.randomNumber(RC_SIZE);
                    int j = ai.stream().boxed().toList().get(randomRC);

                    if (!fs.get(j)) {
                        fs.set(j);
                        BitSet bj = getRowsCoveredByColumn(j);
                        bj.stream().boxed().forEach(idx -> u[idx]++);
                    }
                });
    }

    private void removeRedundantColumns(BitSet fs, int[] u) {
        int numColumns = fs.cardinality() + 1;
        IntStream.range(1, numColumns)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(t -> {
                    int randomNum = cUtils.randomNumber(t);
                    int j = fs.stream().boxed().toList().get(randomNum);
                    BitSet bj = getRowsCoveredByColumn(j);

                    List<Integer> rowsCoveredByOneColumn =
                            bj.stream().boxed().filter(i -> u[i] < 2).toList();

                    if (rowsCoveredByOneColumn.isEmpty()) {
                        fs.clear(j);
                        bj.stream().boxed().forEach(idx -> u[idx]--);
                    }
                });
    }

}