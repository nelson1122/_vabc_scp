package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;
import main.java.variables.ScpVars;

import java.util.*;

import static main.java.variables.ScpVars.*;

public class RandomHeuristic {
    private final CommonUtils cUtils;

    public RandomHeuristic(AbcVars v) {
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet fs = new BitSet();
        int randomColumn = cUtils.randomNumber(getCOLUMNS());
        fs.set(randomColumn);

        BitSet uncoveredRows = cUtils.findUncoveredRows(fs);
        while (!uncoveredRows.isEmpty()) {
            int j = applyHeuristic(fs);
            fs.set(j);
            uncoveredRows = cUtils.findUncoveredRows(fs);
        }
//        removeRepeatedColumns(fs);
        return fs;
    }

    private int applyHeuristic(BitSet xj) {
        BitSet uncoveredRows = cUtils.findUncoveredRows(xj);
        List<Integer> lRows = uncoveredRows.stream()
                .boxed()
                .map(i -> {
                    int Li = getColumnsCoveringRow(i).size();
                    return new Tuple2<>(i, (double) 1 / Li);
                })
                .sorted(Comparator.comparingDouble(Tuple2::getT2))
                .limit(10)
                .map(Tuple2::getT1)
                .toList();

        List<Integer> lColumns = lRows.stream()
                .map(ScpVars::getColumnsCoveringRow)
                .map(ai -> ai.stream().boxed().toList())
                .flatMap(Collection::stream)
                .distinct()
                .map(j -> {
                    BitSet Mj = getRowsCoveredByColumn(j);
                    Mj.and(uncoveredRows);
                    return new Tuple2<>(j, (double) getCost(j) / Mj.cardinality());
                })
                .sorted(Comparator.comparingDouble(Tuple2::getT2))
                .limit(5)
                .map(Tuple2::getT1)
                .toList();

        int randomColumn = cUtils.randomNumber(5);
        return lColumns.get(randomColumn);
    }

    private void removeRepeatedColumns(BitSet fs) {
        int[] u = new int[getROWS()];
        fs.stream()
                .boxed()
                .forEach(j -> {
                    BitSet bj = getRowsCoveredByColumn(j);
                    bj.stream().boxed().forEach(i -> u[i]++);
                });

        fs.stream()
                .boxed()
                .sorted(Collections.reverseOrder())
                .forEach(j -> {
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
