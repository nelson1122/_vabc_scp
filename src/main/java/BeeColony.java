package main.java;


import main.java.utils.BeeUtils;
import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static main.java.config.ParamsConfig.EMPLOYED_BEES;
import static main.java.config.ParamsConfig.FOOD_NUMBER;
import static main.java.config.ParamsConfig.LIMIT;
import static main.java.config.ParamsConfig.ONLOOKER_BEES;

public class BeeColony {
    private AbcVars vr;
    private Repair repair;
    private CommonUtils cUtils;
    private BeeUtils bUtils;
    private Initialization initialization;
    private LocalSearch localSearch;

    public BeeColony() {
    }

    public BeeColony(AbcVars vr) {
        this.vr = vr;
        this.repair = new Repair(vr);
        this.cUtils = new CommonUtils(vr);
        this.bUtils = new BeeUtils(vr);
        this.initialization = new Initialization(vr);
        this.localSearch = new LocalSearch(vr);
    }

    public void initial() {
        vr.setFOODS(new ArrayList<>());
        vr.setFITNESS(new ArrayList<>());
        vr.setTRIAL(new int[FOOD_NUMBER]);
        vr.setPROB(Arrays.asList(new Double[FOOD_NUMBER]));

        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(fs -> {
                    BitSet newFoodSource = initialization.createSolution();
                    vr.addFoodSource(newFoodSource);
                    vr.addFitness(cUtils.calculateFitnessOne(newFoodSource));
                    vr.setTrial(fs, 0);
                });

        vr.setGLOBALMIN(vr.getFitness(0));
        vr.setGLOBALPARAMS(vr.getFoodSource(0));
    }

    public void sendEmployedBees() {
        IntStream.range(0, EMPLOYED_BEES)
                .boxed()
                .forEach(foodNum -> {
                    BitSet fs = vr.getFoodSource(foodNum);

                    int rIndex = cUtils.randomFoodSource(foodNum);
                    BitSet rfs = vr.getFoodSource(rIndex);

                    BitSet distinctColumns = cUtils.findDistinctColumns(fs, rfs);

                    if (!distinctColumns.isEmpty()) {
                        bUtils.addColumns(fs, rfs);
                        bUtils.dropColumns(fs);
                        BitSet uncoveredRows = cUtils.findUncoveredRows(fs);
                        if (!uncoveredRows.isEmpty()) {
                            repair.applyRepairSolution(fs, uncoveredRows);
                        }

                        fs = localSearch.apply(fs);

                        memorizeSource(fs, foodNum);

                    } else {
                        generateScoutBee(foodNum);
                    }
                });
    }

    public void sendOnlookerBees() {
        AtomicInteger foodNumber = new AtomicInteger(0);

        IntStream.range(0, ONLOOKER_BEES)
                .boxed()
                .forEach(t -> {
                    double r = vr.getRANDOM().nextDouble();
//                    double rNum = cUtils.roundDouble(r);

                    double cumulativeProbability = 0.0;
                    for (int fs = 0; fs < FOOD_NUMBER; fs++) {
                        cumulativeProbability += vr.getProbabilityValue(fs);
                        if (r < cumulativeProbability) {
                            foodNumber.set(vr.getProbabilityIndex(fs));
                            break;
                        }
                    }

                    BitSet fs = vr.getFoodSource(foodNumber.get());
                    BitSet distinctColumns = cUtils.getColumnsRandomFoodSource(fs, foodNumber.get());
                    bUtils.addColumns(fs, distinctColumns);
                    bUtils.dropColumns(fs);
                    BitSet uncoveredRows = cUtils.findUncoveredRows(fs);
                    if (!uncoveredRows.isEmpty()) {
                        repair.applyRepairSolution(fs, uncoveredRows);
                    }

                    fs = localSearch.apply(fs);

                    memorizeSource(fs, foodNumber.get());
                });
    }

    public void sendScoutBees() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .filter(foodNumber -> vr.getTrial(foodNumber) >= LIMIT)
                .forEach(foodNumber -> {
                    BitSet newFoodSource = initialization.createSolution();
                    int fitness = cUtils.calculateFitnessOne(newFoodSource);
                    vr.setFoodSource(foodNumber, newFoodSource);
                    vr.setFitness(foodNumber, fitness);
                    vr.setTrial(foodNumber, 0);
                });
    }


    private void generateScoutBee(int foodNum) {
        BitSet newFoodSource = initialization.createSolution();
        vr.setFoodSource(foodNum, newFoodSource);
        vr.setFitness(foodNum, cUtils.calculateFitnessOne(newFoodSource));
        vr.setTrial(foodNum, 0);
    }

    public void memorizeBestSource() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(foodNum -> {
                    int fitness = vr.getFitness(foodNum);
                    if (vr.getGLOBALMIN() == fitness) {
                        BitSet currentFS = vr.getFoodSource(foodNum);
                        BitSet currentBestFS = vr.getGLOBALPARAMS();
                        int f = cUtils.calculateFitnessTwo(currentFS);
                        int fGlobal = cUtils.calculateFitnessTwo(currentBestFS);
                        if (fGlobal > f) {
                            vr.setGLOBALPARAMS(vr.getFoodSource(foodNum));
                        }
                    } else if (vr.getGLOBALMIN() > fitness) {
                        vr.setGLOBALMIN(fitness);
                        vr.setGLOBALPARAMS(vr.getFoodSource(foodNum));
                    }
                });
    }

    private void memorizeSource(BitSet newfs, int foodNum) {
        int currFitness = vr.getFitness(foodNum);
        int newFitness = cUtils.calculateFitnessOne(newfs);

        if (currFitness > newFitness) {
            vr.setFoodSource(foodNum, (BitSet) newfs.clone());
            vr.setFitness(foodNum, newFitness);
            vr.setTrial(foodNum, 0);
        } else if (currFitness == newFitness) {
            int newFitnessTwo = cUtils.calculateFitnessTwo(newfs);
            int currFitnessTwo = cUtils.calculateFitnessTwo(vr.getFoodSource(foodNum));
            if (currFitnessTwo > newFitnessTwo) {
                vr.setFoodSource(foodNum, (BitSet) newfs.clone());
            }
        } else {
            vr.incrementTrial(foodNum);
        }
    }

    public void calculateProbabilitiesOne() {
        double sumFitness = 0d;
        for (int i = 0; i < FOOD_NUMBER; i++) {
            sumFitness += vr.getFitness(i);
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = (double) vr.getFitness(i) / sumFitness;
            vr.setProbability(i, result);
        }

        List<Tuple2<Integer, Double>> probSorted =
                IntStream.range(0, FOOD_NUMBER)
                        .boxed()
                        .map(p -> new Tuple2<>(p, vr.getProbability(p)))
                        .sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                        .toList();
        vr.setPROBSRW(probSorted);
    }

    public void calculateProbabilitiesTwo() {
        double maxfit = vr.getFitness(0);
        for (int i = 0; i < FOOD_NUMBER; i++) {
            if (vr.getFitness(i) > maxfit) {
                maxfit = vr.getFitness(i);
            }
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = (0.9 * (vr.getFitness(i) / maxfit)) + 0.1;
            vr.setProbability(i, result);
        }
    }
}
