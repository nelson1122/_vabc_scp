package main.java.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static main.java.config.Parameters.MAX_CYCLE;
import static main.java.config.Parameters.RUNTIME;

public class Logger {
    public SimpleDateFormat FORMAT;
    private int[] RUNS;
    private int[] GLOBALMINS;
    private int[] SEEDS;

    public Logger() {
        FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        RUNS = new int[RUNTIME];
        GLOBALMINS = new int[RUNTIME];
        SEEDS = new int[RUNTIME];
    }

    public void addProgress(int run) {
        this.RUNS[run]++;
    }

    public void setGlobalMin(int run, int value) {
        this.GLOBALMINS[run] = value;
    }

    public void setSEED(int run, int value) {
        this.SEEDS[run] = value;
    }

    public void log(Tuple3<Integer, Integer, BitSet> result) {
        log((result.getT1() + 1) + " run: " + result.getT2());
        List<String> indexes = result.getT3().stream()
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void log(String message) {
        System.out.printf("%s ==> %s%n", FORMAT.format(new Date()), message);
    }

    public void printProgress(int run, int iter) {
        iter++;
        double value = ((double) (iter) / MAX_CYCLE) * 100;
        String percentage = String.format("%.1f", value);
        System.out.print("[ run:" + ++run + " | iter:" + iter + " ] => " + percentage + "%\r");
    }

    public void log(int run, int globalMin, BitSet globalParams) {
        log((run + 1) + ".run:" + globalMin);
        List<String> indexes = globalParams.stream()
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void log() {
        RUNS = new int[RUNTIME];
        GLOBALMINS = new int[RUNTIME];
        List<String> logs = buildLog();
        System.out.print(String.join("", logs));
    }

    public void start(ForkJoinPool forkJoinPool) throws InterruptedException {
        while (!forkJoinPool.isTerminated()) {
            Thread.sleep(2000);
            List<String> logs = buildLog();
            System.out.print(String.format("\033[%dA", 10));
            System.out.print(String.join("", logs));
        }
    }

    private List<String> buildLog() {
        List<String> logs = new ArrayList<>();
        for (int x = 0; x < RUNS.length; x++) {
            String progress = "";
            for (int y = 0; y < RUNS[x] / 10; y++) {
                progress = progress.concat("·");
            }
            double progDecimal = ((double) RUNS[x] / MAX_CYCLE) * 100.0;
            progress = FORMAT.format(new Date()) + " [ " +
                    "run " + x + " | " +
                    "seed: " + SEEDS[x] + " | " +
                    "iter: " + RUNS[x] + " | " +
                    "Best: " + GLOBALMINS[x] + " ] ==> " +
                    progress.concat(" " + Math.round(progDecimal * 100) / 100.0 + "%\n");
            logs.add(progress);
        }
        return logs;
    }

    public void printLog(int x) {
        String progress = "";
        for (int y = 0; y < RUNS[x] / 10; y++) {
            progress = progress.concat("·");
        }
        double progDecimal = ((double) RUNS[x] / MAX_CYCLE) * 100.0;
        progress = FORMAT.format(new Date()) + " [ run " + x + " | iter: " + RUNS[x] + " | Best: " + GLOBALMINS[x] + " ] ==> " +
                progress.concat(" " + Math.round(progDecimal * 100) / 100.0 + "%");
        System.out.println(progress);
    }

    public void printSolution(BitSet result) {
        List<String> indexes = result.stream()
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void printLog2(int x) {
        String progress = "";
        for (int y = 0; y < RUNS[x] / 10; y++) {
            progress = progress.concat("·");
        }
        double progDecimal = ((double) RUNS[x] / MAX_CYCLE) * 100.0;
        progress = FORMAT.format(new Date()) + " [ " +
                "run " + x + " | " +
                "seed: " + SEEDS[x] + " | " +
                "iter: " + RUNS[x] + " | " +
                "Best: " + GLOBALMINS[x] + " ] ==> " +
                progress.concat(" " + Math.round(progDecimal * 100) / 100.0 + "%");
        System.out.print(String.format("\033[%dA", 0));
        System.out.println(progress);
    }
}
