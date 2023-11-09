package main.java;

import main.java.config.EnvConfig;
import main.java.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

import static main.java.variables.ScpVars.*;


public class Problem {
    private static final Logger logger = new Logger();

    private Problem() {
    }

    public static void read(String filePath) throws IOException {
        logger.log("Loading problem [" + filePath + "] ...");
        Scanner scanner = readFile(filePath);

        setROWS(scanner.nextInt());
        setCOLUMNS(scanner.nextInt());
        setCOSTS(new ArrayList<>());

        setCOLUMNINTS();
        setROWINTS();

        setCOLUMNSCOVERINGROW(new ArrayList<>());
        setROWSCOVEREDBYCOLUMN(new ArrayList<>());
        setRATIOCOSTROWSCOVERED(new double[getCOLUMNS()]);

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

        // calculate ratio cost(j) / rowscoveredbycolumn(j)
        for (int j = 0; j < getCOLUMNS(); j++) {
            BitSet bj = getRowsCoveredByColumn(j);
            double ratio = (double) getCost(j) / bj.cardinality();
            setRatioCostRowsCovered(j, ratio);
        }

        scanner.close();
        logger.log("Problem [" + filePath + "] has been loaded.");
    }


    private static Scanner readFile(String filePath) throws IOException {
        if (EnvConfig.isDev()) {
            File file = new File(filePath);
            return new Scanner(file);
        } else {
            InputStream file = Problem.class.getResourceAsStream(filePath);
            return new Scanner(file);
        }
    }
}
