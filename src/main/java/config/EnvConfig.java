package main.java.config;

import main.java.variables.ScpVars;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static main.java.config.ParamsConfig.RUNTIME;
import static main.java.variables.ScpVars.setInstance;

public class EnvConfig {
    private static boolean dev = false;
    private static boolean multithread = true;
    private static boolean randseed = false;
    private static int initialize = 0;
    private static int localsearch = 0;
    private static String initializeName;
    private static String localSearchName;
    private static int[] seeds;

    private EnvConfig() {
    }

    public static boolean isDev() {
        return dev;
    }

    public static boolean isRandseed() {
        return randseed;
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

    public static int getSeed(int run) {
        return seeds[run];
    }

    public static void setEnvironment() {
        dev = System.getenv().get("dev") != null ?
                Boolean.parseBoolean(System.getenv().get("dev")) : dev;
        randseed = System.getenv().get("randseed") != null ?
                Boolean.parseBoolean(System.getenv().get("randseed")) : randseed;
        multithread = System.getenv().get("multithread") != null ?
                Boolean.parseBoolean(System.getenv().get("multithread")) : multithread;
        initialize = System.getenv().get("initialize") != null ?
                Integer.parseInt(System.getenv().get("initialize")) : initialize;
        localsearch = System.getenv().get("localsearch") != null ?
                Integer.parseInt(System.getenv().get("localsearch")) : localsearch;

        setInitializeName();
        setLocalSearchName();
        setInstances();
        setSeeds();
    }

    private static void setInitializeName() {
        Map<Integer, String> names = new HashMap<>();
        names.put(0, "ABCSCP");
        names.put(1, "RANDOM");
        names.put(2, "RHEURISTIC");
        names.put(3, "ITERCONSTRUC");
        initializeName = names.get(initialize);
    }

    private static void setLocalSearchName() {
        Map<Integer, String> names = new HashMap<>();
        names.put(0, "NLS");
        names.put(1, "ABCSCP");
        names.put(2, "RWMLS");
        names.put(3, "ITERLS");
        localSearchName = names.get(localsearch);
    }

    private static void setInstances() {
        ScpVars.setINSTANCES(new HashMap<>());
//        setInstance("scp41", 429);
//
//        setInstance("scpnre1", 29);
//        setInstance("scpnre2", 30);
//        setInstance("scpnre3", 27);
//        setInstance("scpnre4", 28);
//        setInstance("scpnre5", 28);
//
//        setInstance("scpnrf1", 14);
//        setInstance("scpnrf2", 15);
//        setInstance("scpnrf3", 14);
//        setInstance("scpnrf4", 14);
//        setInstance("scpnrf5", 13);

//        setInstance("scpnrg1", 176);
//        setInstance("scpnrg2", 154);
        setInstance("scpnrg3", 166);
        setInstance("scpnrg4", 168);
//        setInstance("scpnrg5", 168);

        setInstance("scpnrh1", 63);
        setInstance("scpnrh2", 63);
        setInstance("scpnrh3", 59);
        setInstance("scpnrh4", 58);
//        setInstance("scpnrh5", 55);
    }

    private static void setSeeds() {
        seeds = new int[RUNTIME];
        if (randseed) {
            var random = new Random();
            for (int run = 0; run < RUNTIME; run++) {
                seeds[run] = random.nextInt(8000000, 9000000);
            }
        } else {
            seeds[0] = 8261063;
            seeds[1] = 8448565;
            seeds[2] = 8111773;
            seeds[3] = 8125663;
            seeds[4] = 8752766;
            seeds[5] = 8198653;
            seeds[6] = 8645389;
            seeds[7] = 8712653;
            seeds[8] = 8202972;
            seeds[9] = 8833610;
        }
    }
}
