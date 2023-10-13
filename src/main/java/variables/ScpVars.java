package main.java.variables;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ScpVars {
    public static int ROWS;
    public static int COLUMNS;
    public static List<Integer> COSTS;
    public static List<BitSet> COLUMNSCOVERINGROW;
    public static List<BitSet> ROWSCOVEREDBYCOLUMN;
    public static Map<String, Integer> INSTANCES;
    public static BitSet ROWINTS;
    public static List<Integer> COLUMNINTS;

    public static double[] RATIOCOSTROWSCOVERED;

    private ScpVars() {
    }

    public static int getROWS() {
        return ROWS;
    }

    public static void setROWS(int ROWS) {
        ScpVars.ROWS = ROWS;
    }

    public static int getCOLUMNS() {
        return COLUMNS;
    }

    public static void setCOLUMNS(int COLUMNS) {
        ScpVars.COLUMNS = COLUMNS;
    }

    public static List<Integer> getCOSTS() {
        return COSTS;
    }

    public static void setCOSTS(List<Integer> COSTS) {
        ScpVars.COSTS = COSTS;
    }

    public static void setCOLUMNSCOVERINGROW(List<BitSet> COLUMNSCOVERINGROW) {
        ScpVars.COLUMNSCOVERINGROW = COLUMNSCOVERINGROW;
    }

    public static void setROWSCOVEREDBYCOLUMN(List<BitSet> ROWSCOVEREDBYCOLUMN) {
        ScpVars.ROWSCOVEREDBYCOLUMN = ROWSCOVEREDBYCOLUMN;
    }

    public static void setINSTANCES(Map<String, Integer> INSTANCES) {
        ScpVars.INSTANCES = INSTANCES;
    }

    public static Map<String, Integer> getINSTANCES() {
        return ScpVars.INSTANCES;
    }

    public static void setCOLUMNINTS() {
        ScpVars.COLUMNINTS = IntStream.range(0, COLUMNS)
                .boxed()
                .toList();
    }

    public static List<Integer> getCOLUMNINTS() {
        return ScpVars.COLUMNINTS;
    }

    public static void setROWINTS() {
        ROWINTS = IntStream.range(0, ROWS)
                .boxed()
                .collect(BitSet::new, BitSet::set, BitSet::or);
    }

    public static BitSet getROWINTS() {
        return (BitSet) ROWINTS.clone();
    }

    public static double[] getRATIOCOSTROWSCOVERED() {
        return RATIOCOSTROWSCOVERED;
    }

    public static void setRATIOCOSTROWSCOVERED(double[] RATIOCOSTROWSCOVERED) {
        ScpVars.RATIOCOSTROWSCOVERED = RATIOCOSTROWSCOVERED;
    }

    // Custom methods
    public static void setCost(int value) {
        COSTS.add(value);
    }

    public static Integer getCost(int j) {
        return COSTS.get(j);
    }

    public static BitSet getColumnsCoveringRow(int i) {
        return (BitSet) COLUMNSCOVERINGROW.get(i).clone();
    }

    public static void setColumnsCoveringRow(BitSet ai) {
        COLUMNSCOVERINGROW.add(ai);
    }

    public static BitSet getRowsCoveredByColumn(int j) {
        return (BitSet) ROWSCOVEREDBYCOLUMN.get(j).clone();
    }

    public static void setRowsCoveredByColumn(BitSet bj) {
        ROWSCOVEREDBYCOLUMN.add(bj);
    }

    public static void setInstance(String key, Integer value) {
        INSTANCES.put(key, value);
    }

    public static Integer getBest(String key) {
        return INSTANCES.get(key);
    }

    public static void setRatioCostRowsCovered(int j, double value) {
        ScpVars.RATIOCOSTROWSCOVERED[j] = value;
    }

    public static double getRatioCostRowsCovered(int j) {
        return ScpVars.RATIOCOSTROWSCOVERED[j];
    }
}
