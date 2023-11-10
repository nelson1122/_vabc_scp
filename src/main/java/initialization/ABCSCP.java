package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.List;

import static main.java.config.ParamsConfig.RC_SIZE;
import static main.java.variables.ScpVars.*;


public class ABCSCP {

    private final CommonUtils cUtils;

    public ABCSCP(AbcVars v) {
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet fs = new BitSet();
        int[] u = new int[getROWS()];

        generateSolution(fs, u);
        removeRedundantColumns(fs, u);

        return fs;
    }

    private void generateSolution(BitSet fs, int[] u) {
        for (int i = 0; i < getROWS(); i++) {
            BitSet ai = getColumnsCoveringRow(i);
            int randomRC = cUtils.randomNumber(RC_SIZE);
            int j = ai.stream().boxed().toList().get(randomRC);

            if (!fs.get(j)) {
                fs.set(j);
                BitSet bj = getRowsCoveredByColumn(j);
                bj.stream().boxed().forEach(idx -> u[idx]++);
            }
        }
    }

    private void removeRedundantColumns(BitSet fs, int[] u) {
        int t = fs.cardinality();
        while (t > 0) {
            int randomNum = cUtils.randomNumber(t);
            int j = fs.stream().boxed().toList().get(randomNum);
            BitSet bj = getRowsCoveredByColumn(j);

            List<Integer> rowsCoveredByOneColumn =
                    bj.stream().boxed().filter(i -> u[i] < 2).toList();

            if (rowsCoveredByOneColumn.isEmpty()) {
                fs.clear(j);
                bj.stream().boxed().forEach(idx -> u[idx]--);
            }
            t--;
        }
    }

}