package main.java;

import main.java.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Scanner;

import static main.java.variables.ScpVars.getCOLUMNS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getROWS;
import static main.java.variables.ScpVars.setBESTS;
import static main.java.variables.ScpVars.setBest;
import static main.java.variables.ScpVars.setCOLUMNS;
import static main.java.variables.ScpVars.setCOLUMNSCOVERINGROW;
import static main.java.variables.ScpVars.setCOSTS;
import static main.java.variables.ScpVars.setColumnsCoveringRow;
import static main.java.variables.ScpVars.setCost;
import static main.java.variables.ScpVars.setROWS;
import static main.java.variables.ScpVars.setROWSCOVEREDBYCOLUMN;
import static main.java.variables.ScpVars.setRowsCoveredByColumn;


public class Problem {
    private static final Logger logger = new Logger();

    private Problem() {
    }

    public static void read(String filePath) throws IOException {
        logger.log("Loading problem [" + filePath + "] ...");

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        setROWS(scanner.nextInt());
        setCOLUMNS(scanner.nextInt());
        setCOSTS(new ArrayList<>());

        setCOLUMNSCOVERINGROW(new ArrayList<>());
        setROWSCOVEREDBYCOLUMN(new ArrayList<>());


        for (int j = 0; j < getCOLUMNS(); j++) {
            setCost(scanner.nextInt());
        }

        for (int i = 0; i < getROWS(); i++) {
            int numCol = scanner.nextInt();
            BitSet ai = new BitSet();
            for (int j = 0; j < numCol; j++) {
                int column = scanner.nextInt() - 1;
                ai.set(column);
            }
            setColumnsCoveringRow(ai);
        }

        for (int j = 0; j < getCOLUMNS(); j++) {
            BitSet bj = new BitSet();
            for (int i = 0; i < getROWS(); i++) {
                BitSet ai = getColumnsCoveringRow(i);
                if (ai.get(j)) {
                    bj.set(i);
                }
            }
            setRowsCoveredByColumn(bj);
        }

        scanner.close();

        setBKS();

        logger.log("Problem [" + filePath + "] has been loaded.");
    }

    private static void setBKS() {
        setBESTS(new HashMap<>());

        setBest("scpnre1", 29);
        setBest("scpnre2", 30);
        setBest("scpnre3", 27);
        setBest("scpnre4", 28);
        setBest("scpnre5", 28);

        setBest("scpnrf1", 14);
        setBest("scpnrf2", 15);
        setBest("scpnrf3", 14);
        setBest("scpnrf4", 14);
        setBest("scpnrf5", 13);

        setBest("scpnrg1", 176);
        setBest("scpnrg2", 154);
        setBest("scpnrg3", 166);
        setBest("scpnrg4", 168);
        setBest("scpnrg5", 168);

        setBest("scpnrh1", 63);
        setBest("scpnrh2", 63);
        setBest("scpnrh3", 59);
        setBest("scpnrh4", 58);
        setBest("scpnrh5", 55);
    }
}
