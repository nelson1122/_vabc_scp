package main.java.config;

import main.java.variables.ScpVars;

import java.util.HashMap;
import java.util.Map;

import static main.java.variables.ScpVars.setInstance;

public class EnvConfig {
    private static boolean multithread = true;
    private static int initialize = 0;
    private static int localsearch = 0;

    private static String initializeName;

    private static String localSearchName;

    private EnvConfig() {
    }

    public static boolean isMultithread() {
        return multithread;
    }

    public static int getInitialize() {
        return initialize;
    }

    public static int getLocalsearch() {
        return localsearch;
    }

    public static String getInitializeName() {
        return initializeName;
    }

    public static String getLocalSearchName() {
        return localSearchName;
    }

    public static void setEnvironment() {
        multithread = System.getenv().get("multithread") != null ?
                Boolean.parseBoolean(System.getenv().get("multithread")) : multithread;
        initialize = System.getenv().get("initialize") != null ?
                Integer.parseInt(System.getenv().get("initialize")) : initialize;
        localsearch = System.getenv().get("localsearch") != null ?
                Integer.parseInt(System.getenv().get("localsearch")) : localsearch;

        setInitializeName();
        setLocalSearchName();
        setINSTANCES();
    }

    private static void setInitializeName() {
        Map<Integer, String> names = new HashMap<>();
        names.put(0, "ABCSCP");
        names.put(1, "RANDOM");
        names.put(2, "RHEURISTIC");
        initializeName = names.get(initialize);
    }

    private static void setLocalSearchName() {
        Map<Integer, String> names = new HashMap<>();
        names.put(0, "NLS");
        names.put(1, "ABCSCP");
        names.put(2, "RWMLS");
        localSearchName = names.get(localsearch);
    }

    private static void setINSTANCES() {
        ScpVars.setINSTANCES(new HashMap<>());

        setInstance("scpnre1", 29);
        setInstance("scpnre2", 30);
        setInstance("scpnre3", 27);
        setInstance("scpnre4", 28);
        setInstance("scpnre5", 28);

        setInstance("scpnrf1", 14);
        setInstance("scpnrf2", 15);
        setInstance("scpnrf3", 14);
        setInstance("scpnrf4", 14);
        setInstance("scpnrf5", 13);

        setInstance("scpnrg1", 176);
        setInstance("scpnrg2", 154);
        setInstance("scpnrg3", 166);
        setInstance("scpnrg4", 168);
        setInstance("scpnrg5", 168);

        setInstance("scpnrh1", 63);
        setInstance("scpnrh2", 63);
        setInstance("scpnrh3", 59);
        setInstance("scpnrh4", 58);
        setInstance("scpnrh5", 55);

    }
}
