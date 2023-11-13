package main.java.utils;

import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static main.java.config.ParamsConfig.*;


public class BeeUtils {
    private final CommonUtils cUtils;
    private final List<Integer> addedCols;
    private final List<Integer> droppedCols;

    public BeeUtils(AbcVars vr) {
        this.cUtils = new CommonUtils(vr);
        this.addedCols = new ArrayList<>();
        this.droppedCols = new ArrayList<>();
    }

    public void addColumns(BitSet cfs, BitSet rfs) {
        BitSet dCols = cUtils.findDistinctColumns(cfs, rfs);
        List<Integer> distinctColumns = dCols.stream().boxed().toList();

        int n = cfs.cardinality();
        int dc = dCols.cardinality();
        int colAdd;

        if (n > 35) {
            colAdd = Math.min(dc, COL_ADD_1);
        } else {
            colAdd = Math.min(dc, COL_ADD_2);
        }

        while (colAdd > 0) {
            int index = cUtils.randomNumber(dc);
            int j = distinctColumns.get(index);

            if (!addedCols.contains(j)) {
                cfs.set(j);
                addedCols.add(j);
                colAdd--;
            }
        }
    }

    public void dropColumns(BitSet cfs) {
        int n = cfs.cardinality();
        List<Integer> columns = cfs.stream().boxed().toList();

        int colDrop;

        if (n < 5) {
            colDrop = n;
        } else if (n > 35) {
            colDrop = COL_DROP_1;
        } else {
            colDrop = COL_DROP_2;
        }

        while (colDrop > 0) {
            int index = cUtils.randomNumber(n);
            int j = columns.get(index);

            if (!addedCols.contains(j) && !droppedCols.contains(j)) {
                cfs.clear(j);
                droppedCols.add(j);
                colDrop--;
            }
        }
        addedCols.clear();
        droppedCols.clear();
    }
}
