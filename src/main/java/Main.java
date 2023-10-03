package main.java;

import main.java.utils.Logger;
import main.java.utils.Tuple3;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

import static main.java.config.Parameters.MAX_CYCLE;
import static main.java.config.Parameters.RUNTIME;
import static main.java.variables.ScpVars.getBest;

public class Main {
    static Logger logger = new Logger();
    static int seed;
    static String[] instances1 = {
            "scpnre1",
            "scpnre2",
            "scpnre3",
            "scpnre4",
            "scpnre5",
            "scpnrg1",
            "scpnrg2",
            "scpnrg3",
            "scpnrg4",
            "scpnrg5"
    };
    static String[] instances2 = {
            "scpnrf1",
            "scpnrf2",
            "scpnrf3",
            "scpnrf4",
            "scpnrf5",
            "scpnrh1",
            "scpnrh2",
            "scpnrh3",
            "scpnrh4",
            "scpnrh5",
    };

    static boolean multithread = false;
    static int variant = 0;
    static String[] instances = instances1;
    static int instancesGroup = 1;


    private Main() {
    }

    public static void main(String[] args) {

        setEnvironment();

        logger.log("Variant of the Artificial Bee Colony Algorithm ABC_SCP to solve the Set Covering Problem");
        logger.log("University of Cauca, 2023");
        logger.log("Algorithm Variant: [" + variant + "]");
        logger.log("Multi-thread: [ " + multithread + " ]");

        if (multithread) {
            logger.log("Instances Group: [ " + (instancesGroup == 1 ? "I" : "II") + " ]");
            runVABCSCPMultiThread();
        } else {
            runVABCSCPMonoThread();
        }

        logger.log("Algorithm has finished!");
    }

    private static void setEnvironment() {
        multithread = System.getenv().get("multithread") != null && Boolean.parseBoolean(System.getenv().get("multithread"));
        variant = System.getenv().get("variant") != null ? Integer.parseInt(System.getenv().get("variant")) : 0;
        instancesGroup = System.getenv().get("instances") != null ? Integer.parseInt(System.getenv().get("group")) : 1;

        if (instancesGroup == 2) {
            instances = instances2;
        } else {
            instances = instances1;
        }
    }

    private static void runVABCSCPMonoThread() {
        String instance = "scpnrg2";
        try {
            Problem.read("src/main/resources/" + instance + ".txt");
            var vr = new AbcVars(140);
            var bee = new BeeColony(vr);
            bee.initial();
            bee.memorizeBestSource();
            for (int iter = 0; iter < MAX_CYCLE; iter++) {
                bee.sendEmployedBees();
                bee.calculateProbabilitiesOne();
                bee.sendOnlookerBees();
                bee.memorizeBestSource();
                bee.sendScoutBees();
                logger.addProgress(0);
                logger.setGlobalMin(0, vr.getGLOBALMIN());
                logger.printLog(0);

                // validate best
                int instanceBest = getBest(instance);
                int globalMin = vr.getGLOBALMIN();

                if (instanceBest == globalMin) {
                    break;
                }
            }
            logger.printSolution(vr.getGLOBALPARAMS());
        } catch (Exception ex) {
            logger.log("Error reading file");
        }
    }

    public static void runVABCSCPMultiThread() {
        for (String instance : instances) {
            try {
                Problem.read("main/resources/" + instance + ".txt");
                logger.log("Problem processing [" + instance + "] has started!");
                logger.log();
                seed = 50;

                ForkJoinPool forkJoinPool = new ForkJoinPool(RUNTIME);
                List<ForkJoinTask<Tuple3<Integer, Integer, BitSet>>> results =
                        IntStream.range(0, RUNTIME)
                                .sorted()
                                .mapToObj(rIndex -> forkJoinPool.submit(() -> {
                                    seed = seed + 80;
                                    logger.setSEED(rIndex, seed);
                                    return runVABCSCP(instance, variant, rIndex, seed);
                                })).toList();

                forkJoinPool.shutdown();
                logger.start(forkJoinPool);
                results.stream()
                        .map(ForkJoinTask::join)
                        .mapToInt(x -> {
                            logger.log(x);
                            return x.getT2();
                        })
                        .average()
                        .ifPresent(average -> logger.log("Runs average: " + average + "\n"));

            } catch (Exception ex) {
                logger.log("Error reading file");
                Thread.currentThread().interrupt();
            }
        }
    }

    private static Tuple3<Integer, Integer, BitSet> runVABCSCP(String instance, int variant, int runtimeIdx, int seed) {
        AbcVars vr = new AbcVars(seed);
        vr.setAlgorithmVariant(variant);

        BeeColony bee = new BeeColony(vr);
        bee.initial();
        bee.memorizeBestSource();
        for (int iter = 0; iter < MAX_CYCLE; iter++) {
            bee.sendEmployedBees();
            bee.calculateProbabilitiesOne();
            bee.sendOnlookerBees();
            bee.memorizeBestSource();
            bee.sendScoutBees();
            logger.addProgress(runtimeIdx);
            logger.setGlobalMin(runtimeIdx, vr.getGLOBALMIN());

            // validate best
            int instanceBest = getBest(instance);
            int globalMin = vr.getGLOBALMIN();

            if (instanceBest == globalMin) {
                break;
            }
        }
        vr.addGlobalMin(vr.getGLOBALMIN());
        return new Tuple3<>(runtimeIdx, vr.getGLOBALMIN(), vr.getGLOBALPARAMS());
    }
}
