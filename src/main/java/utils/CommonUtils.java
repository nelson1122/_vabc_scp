package main.java.utils;

import main.java.variables.AbcVars;
import main.java.variables.ScpVars;

import java.util.BitSet;
import java.util.Optional;

import static main.java.config.ParamsConfig.FOOD_NUMBER;
import static main.java.variables.ScpVars.getROWINTS;
import static main.java.variables.ScpVars.getROWS;


public class CommonUtils {
    private final AbcVars vr;

    public CommonUtils(AbcVars vr) {
        this.vr = vr;
    }

    public int randomNumber(int high) {
        return vr.getRANDOM().nextInt(high);
    }

    public double roundDouble(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public int calculateFitnessOne(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(ScpVars::getCost)
                .reduce(Integer::sum)
                .get();
    }

    public int calculateFitnessTwo(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(ScpVars::getRowsCoveredByColumn)
                .map(x -> x.stream().boxed().toList().size())
                .reduce(Integer::sum)
                .get();
    }

    public int randomFoodSource(int i) {
        int randomFood = randomNumber(FOOD_NUMBER);
        if (i != randomFood) {
            return randomFood;
        }
        return randomFoodSource(i);
    }

    public BitSet findDistinctColumns(BitSet cfs, BitSet rfs) {
        BitSet s1 = (BitSet) cfs.clone();
        BitSet s2 = (BitSet) rfs.clone();
        s2.andNot(s1);
        return s2;
    }

    public BitSet findUncoveredRows(BitSet cfs) {
        Optional<BitSet> optCoveredRows = cfs.stream()
                .boxed()
                .map(ScpVars::getRowsCoveredByColumn)
                .reduce((x, y) -> {
                    x.or(y);
                    return x;
                });
        if (optCoveredRows.isPresent()) {
            BitSet rows = getROWINTS();
            rows.andNot(optCoveredRows.get());
            return rows;
        }
        return getROWINTS();
    }

    public BitSet getColumnsRandomFoodSource(BitSet cfs, int i) {
        int randomFoodS = randomFoodSource(i);
        BitSet distinctColumns = findDistinctColumns(cfs, vr.getFoodSource(randomFoodS));
        if (!distinctColumns.isEmpty()) {
            return distinctColumns;
        }
        return getColumnsRandomFoodSource(cfs, i);
    }
}
