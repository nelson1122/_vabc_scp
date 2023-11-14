package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static main.java.variables.ScpVars.*;

public class IterativeConstruction {

    private final AbcVars vr;
    private final CommonUtils cUtils;

    public IterativeConstruction(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
    }

    public BitSet createSolution() {
        BitSet xj = new BitSet();
        int j = 0;
        List<BitSet> mapList = getROWINTS()
                .stream()
                .boxed()
                .map(i -> {
                    BitSet cList = getColumnsCoveringRow(i);
                    return new Tuple2<>(cList, cList.cardinality());
                })
                .sorted(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .collect(Collectors.toList());

        while (!mapList.isEmpty()) {
            int rFunction = cUtils.randomNumber(6);
            List<Tuple2<Integer, Double>> cols = mapList.get(0)
                    .stream()
                    .boxed()
                    .map(c -> new Tuple2<>(c, calculateFunction(xj, c, rFunction)))
                    .sorted(Comparator.comparing(Tuple2::getT2))
                    .toList();

            double r = vr.getNextDouble();
//            double rNum = cUtils.roundDouble(r);

            double ratio = 100.0 / cols.size();

            if (r < ratio) {
                double sumFunction = cols.stream().mapToDouble(Tuple2::getT2).sum();
                double r2 = vr.getNextDouble();
                double cumulativeProbability = 0.0;

                for (Tuple2<Integer, Double> c : cols) {
                    cumulativeProbability += (c.getT2() / sumFunction);
                    if (r2 < cumulativeProbability) {
                        j = c.getT1();
                        break;
                    }
                }
                xj.set(j);
            } else {
                Optional<Integer> optCol = cols.stream()
                        .min(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1);

                if (optCol.isPresent()) {
                    j = optCol.get();
                }
                xj.set(j);
            }
            updateMap(mapList, j);
        }
        return xj;
    }

    private double calculateFunction(BitSet xj, int j, int rFunction) {
        BitSet uncoveredRows = cUtils.findUncoveredRows(xj);
        BitSet rowsCoveredByColumn = getRowsCoveredByColumn(j);
        uncoveredRows.and(rowsCoveredByColumn);

        int pj = uncoveredRows.cardinality();
        return switch (rFunction) {
            case 0 -> getCost(j) * 1.0 / pj;
            case 1 -> getCost(j) * 1.0 / Math.log(1.0 + pj);
            case 2 -> getCost(j) * 1.0 / Math.sqrt(pj);
            case 3 -> getCost(j) * 1.0 / Math.pow(pj, 2);
            case 4 -> Math.sqrt(getCost(j)) / pj;
            case 5 -> getCost(j) * 1.0 / (pj * Math.log(pj));
            default -> 0.0;
        };
    }

    private void updateMap(List<BitSet> mapList, int j) {
        mapList.removeIf(row -> row.get(j));
    }
}