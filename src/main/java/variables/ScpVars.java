package main.java.variables;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class ScpVars {
    public static int ROWS;
    public static int COLUMNS;
    public static List<Integer> COSTS;
    public static List<BitSet> COLUMNSCOVERINGROW;
    public static List<BitSet> ROWSCOVEREDBYCOLUMN;
    public static Map<String, Integer> BESTS;

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

    public static Map<String, Integer> getBESTS() {
        return BESTS;
    }

    public static void setBESTS(Map<String, Integer> BESTS) {
        ScpVars.BESTS = BESTS;
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

    public static void setBest(String key, Integer value) {
        BESTS.put(key, value);
    }

    public static Integer getBest(String key) {
        return BESTS.get(key);
    }
}
