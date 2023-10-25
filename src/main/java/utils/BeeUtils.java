package main.java.utils;

import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import static main.java.config.ParamsConfig.COL_ADD_1;
import static main.java.config.ParamsConfig.COL_ADD_2;
import static main.java.config.ParamsConfig.COL_DROP_1;
import static main.java.config.ParamsConfig.COL_DROP_2;


public class BeeUtils {
    private final AbcVars vr;
    private final CommonUtils cUtils;

    public BeeUtils(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
    }

    public void addColumns(BitSet cfs, BitSet rfs) {
        BitSet dCols = cUtils.findDistinctColumns(cfs, rfs);
        List<Integer> distinctColumns = dCols.stream().boxed().toList();

        int n = cfs.cardinality();
        int dc = dCols.cardinality();
        int colAdd;

        boolean addAllColumns = false;
        if (n > 35) {
            colAdd = COL_ADD_1;
            if (dc <= COL_ADD_1) {
                addAllColumns = true;
            }
        } else {
            colAdd = COL_ADD_2;
            if (dc <= COL_ADD_2) {
                addAllColumns = true;
            }
        }

        if (addAllColumns) {
            distinctColumns.forEach(cfs::set);
        } else {
            IntStream.range(0, dc)
                    .map(num -> vr.getRANDOM().nextInt(dc))
                    .distinct()
                    .map(distinctColumns::get)
                    .limit(colAdd)
                    .boxed()
                    .forEach(cfs::set);
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

        IntStream.range(0, n)
                .map(num -> vr.getRANDOM().nextInt(n))
                .distinct()
                .map(columns::get)
                .limit(colDrop)
                .boxed()
                .forEach(cfs::clear);
    }
}
