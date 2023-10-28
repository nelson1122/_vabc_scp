package main.java.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.config.ParamsConfig.MAX_CYCLE;
import static main.java.config.ParamsConfig.RUNTIME;

public class Logger {
    public SimpleDateFormat FORMAT;
    public Date[] DATEINIT;
    public Date[] DATEPROG;
    private int[] RUNS;
    private int[] GLOBALMINS;
    private int[] SEEDS;
    private BitSet[] GLOBALPARAMS;

    public Logger() {
        FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        RUNS = new int[RUNTIME];
        GLOBALMINS = new int[RUNTIME];
        SEEDS = new int[RUNTIME];

        setDateInit();
        setDateProg();

        GLOBALPARAMS = new BitSet[RUNTIME];
    }

    private void setDateInit() {
        DATEINIT = new Date[]{new Date(), new Date(), new Date(), new Date(),
                new Date(), new Date(), new Date(),
                new Date(), new Date(), new Date()};
    }

    private void setDateProg() {
        DATEPROG = new Date[]{new Date(), new Date(), new Date(), new Date(),
                new Date(), new Date(), new Date(),
                new Date(), new Date(), new Date()};
    }

    public void addProgress(int run) {
        this.RUNS[run]++;
    }

    public void setGlobalMin(int run, Date date, int value) {
        this.DATEPROG[run] = date;
        this.GLOBALMINS[run] = value;
    }

    public void setSeed(int run, int value) {
        this.SEEDS[run] = value;
    }

    public void setDateInit(int runtime) {
        DATEINIT[runtime] = new Date();
    }

    public void log(Tuple3<Integer, Integer, BitSet> result) {
        List<String> indexes = result.getT3().stream()
                .map(x -> x + 1)
                .mapToObj(String::valueOf)
                .toList();
        logSolution("run " + result.getT1() + " => {" + String.join(",", indexes) + "}");
    }

    public void log(String message) {
        System.out.printf("%s ==> %s%n", FORMAT.format(new Date()), message);
    }

    public void logSolution(String message) {
        System.out.printf("%s%n%n", message);
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
                .toList();
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void log() {
        RUNS = new int[RUNTIME];
        GLOBALMINS = new int[RUNTIME];
        setDateInit();
        setDateProg();

        List<String> logs = buildLog2();
        System.out.print(String.join("", logs));
    }

    public void start(ForkJoinPool forkJoinPool) throws InterruptedException {
        while (!forkJoinPool.isTerminated()) {
            Thread.sleep(1000);
            List<String> logs = buildLog2();
            System.out.print(String.format("\033[%dA", 12));
            System.out.print(String.join("", logs));
        }
    }

    public void addGlobalParams(int run, BitSet solution) {
        GLOBALPARAMS[run] = solution;
    }

    public void printInitialLog() {
        RUNS = new int[RUNTIME];
        GLOBALMINS = new int[RUNTIME];
        setDateInit();
        setDateProg();

        List<String> logs = buildLog2();
        System.out.print(String.join("", logs));
    }

    public void printLog() {
        List<String> logs = buildLog2();
        System.out.print(String.format("\033[%dA", 12));
        System.out.print(String.join("", logs));
    }

    public void printSolutions() {
        for (int run = 0; run < RUNTIME; run++) {
            List<String> indexes = GLOBALPARAMS[run].stream()
                    .map(x -> x + 1)
                    .mapToObj(String::valueOf)
                    .toList();
            logSolution("run " + run + " => {" + String.join(", ", indexes) + "}");
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
            progress = FORMAT.format(DATEPROG[x]) + " [ " +
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


    public List<String> buildLog2() {
        List<String> logs = new ArrayList<>();
        String header = String.format("                        | run   | seed  | iter  | min   | ttb        | prog  %n");
        logs.add(header);

        for (int runtime = 0; runtime < RUNS.length; runtime++) {
            double progress = ((double) RUNS[runtime] / MAX_CYCLE) * 100.0;
            double progressRound = Math.round(progress * 100) / 100.0;

            String progressLine = "";
            for (int y = 0; y < RUNS[runtime] / 10; y++) {
                progressLine = progressLine.concat("·");
            }

            String line = String.format("%-20s | %-5d | %-5d | %-5d | %-5d | %-10d | %-5s %n",
                    FORMAT.format(DATEPROG[runtime]),
                    runtime,
                    SEEDS[runtime],
                    RUNS[runtime],
                    GLOBALMINS[runtime],
                    secondBetweenDates(runtime),
                    progressLine + " " + progressRound + " %");
            logs.add(line);
        }

        String resultLine = String.format("                                                | %-5s | %-10s |       %n",
                (Math.round(getMinAverage() * 100) / 100.0) + "",
                (Math.round(getTimeToBestAverage() * 100) / 100.0) + "");

        logs.add(resultLine);

        return logs;
    }

    private long secondBetweenDates(int runtime) {
        return (DATEPROG[runtime].getTime() - DATEINIT[runtime].getTime()) / 1000;
    }

    private double getMinAverage() {
        var average = Arrays.stream(GLOBALMINS)
                .boxed()
                .mapToInt(Integer::intValue)
                .filter(min -> min > 0)
                .average();
        if (average.isPresent()) {
            return average.getAsDouble();
        }
        return 0;
    }

    private double getTimeToBestAverage() {
        var average = IntStream.range(0, RUNTIME)
                .boxed()
                .mapToDouble(rt -> (double) (DATEPROG[rt].getTime() - DATEINIT[rt].getTime()) / 1000)
                .filter(time -> time > 0)
                .average();
        if (average.isPresent()) {
            return average.getAsDouble();
        }
        return 0;
    }
}
