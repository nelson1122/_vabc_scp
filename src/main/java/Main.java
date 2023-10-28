package main.java;

import main.java.config.EnvConfig;
import main.java.utils.Logger;
import main.java.utils.Tuple3;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

import static main.java.config.ParamsConfig.MAX_CYCLE;
import static main.java.config.ParamsConfig.RUNTIME;
import static main.java.variables.ScpVars.getINSTANCES;

public class Main {
    static Logger logger = new Logger();
    static int seed;

    private Main() {
    }

    public static void main(String[] args) {
        EnvConfig.setEnvironment();

        logger.log("Variant of the Artificial Bee Colony Algorithm ABC_SCP to solve the Set Covering Problem");
        logger.log("University of Cauca, 2023");
        logger.log("Initialize Method: " + EnvConfig.getInitializeName() + " | " +
                "LocalSearch Method: " + EnvConfig.getLocalSearchName() + " | " +
                "Multi-thread: " + EnvConfig.isMultithread());

        if (EnvConfig.isMultithread()) {
            runVABCSCPMultiThread();
        } else {
            runVABCSCPMonoThread();
        }

        logger.log("Algorithm has finished!");
    }

    private static void runVABCSCPMonoThread() {
        TreeMap<String, Integer> instances = new TreeMap<>(getINSTANCES());
        instances.forEach((instance, best) -> {
            try {
                Problem.read(EnvConfig.getResourcePath(instance));
                logger.log("Problem processing [" + instance + "] has started..");
                logger.printInitialLog();
                seed = 0;

                for (int run = 0; run < RUNTIME; run++) {
                    seed += 10;
                    logger.setSeed(run, seed);
                    logger.setDateInit(run);

                    var vr = new AbcVars(seed);
                    vr.setInitializeMethod(EnvConfig.getInitialize());
                    vr.setLocalSearchMethod(EnvConfig.getLocalsearch());

                    logger.setSeed(run, seed);
                    logger.setDateInit(run);

                    var bee = new BeeColony(vr);
                    bee.initial();
                    bee.memorizeBestSource();

                    for (int iter = 0; iter < MAX_CYCLE; iter++) {
                        bee.sendEmployedBees();
                        bee.calculateProbabilitiesOne();
                        bee.sendOnlookerBees();
                        bee.memorizeBestSource();
                        bee.sendScoutBees();
                        logger.addProgress(run);
                        logger.setGlobalMin(run, new Date(), vr.getGLOBALMIN());
                        logger.printLog();

                        // validate best
                        int globalMin = vr.getGLOBALMIN();

                        if (best == globalMin) {
                            break;
                        }
                    }

                    logger.addGlobalParams(run, vr.getGLOBALPARAMS());
                }
                logger.printSolutions();
            } catch (Exception ex) {
                logger.log("Error reading file: " + instance);
            }

        });

    }

    public static void runVABCSCPMultiThread() {
        TreeMap<String, Integer> instances = new TreeMap<>(getINSTANCES());
        instances.forEach((instance, best) -> {
            try {
                Problem.read(EnvConfig.getResourcePath(instance));
                logger.log("Problem processing [" + instance + "] has started..");
                logger.log();
                seed = 0;

                ForkJoinPool forkJoinPool = new ForkJoinPool(RUNTIME);
                List<ForkJoinTask<Tuple3<Integer, Integer, BitSet>>> results =
                        IntStream.range(0, RUNTIME)
                                .sorted()
                                .mapToObj(rIndex -> forkJoinPool.submit(() -> {
                                    seed = seed + 10;
                                    logger.setSeed(rIndex, seed);
                                    return runVABCSCP(rIndex, seed, best);
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
                logger.log("Error processing instance..");
                Thread.currentThread().interrupt();
            }
        });
    }

    private static Tuple3<Integer, Integer, BitSet> runVABCSCP(int runtime, int seed, int best) {
        logger.setDateInit(runtime);
        logger.setSeed(runtime, seed);

        var vr = new AbcVars(seed);
        vr.setInitializeMethod(EnvConfig.getInitialize());
        vr.setLocalSearchMethod(EnvConfig.getLocalsearch());

        var bee = new BeeColony(vr);
        bee.initial();
        bee.memorizeBestSource();

        for (int iter = 1; iter <= MAX_CYCLE; iter++) {
            bee.sendEmployedBees();
            bee.calculateProbabilitiesOne();
            bee.sendOnlookerBees();
            bee.memorizeBestSource();
            bee.sendScoutBees();
            logger.addProgress(runtime);
            logger.setGlobalMin(runtime, new Date(), vr.getGLOBALMIN());

            // validate best
            int globalMin = vr.getGLOBALMIN();
            if (best == globalMin) {
                break;
            }
        }

        vr.addGlobalMin(vr.getGLOBALMIN());
        return new Tuple3<>(runtime, vr.getGLOBALMIN(), vr.getGLOBALPARAMS());
    }
}
