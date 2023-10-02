package main.java.variables;


import main.java.utils.Tuple2;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class AbcVars {
    private List<BitSet> FOODS;
    private BitSet GLOBALPARAMS;
    private List<Integer> FITNESS;
    private int[] TRIAL;
    private List<Double> PROB;
    private List<Tuple2<Integer, Double>> PROBSRW;
    private Integer GLOBALMIN;
    private List<Integer> GLOBALMINS;
    private Double MEAN;
    private Random RANDOM;
    private long SEED;

    public AbcVars(long seed) {
        this.SEED = seed;
        this.RANDOM = new Random(seed);
        this.GLOBALMINS = new ArrayList<>();
        this.MEAN = 0d;
    }

    public List<BitSet> getFOODS() {
        return FOODS;
    }

    public void setFOODS(List<BitSet> FOODS) {
        this.FOODS = FOODS;
    }

    public BitSet getGLOBALPARAMS() {
        return (BitSet) GLOBALPARAMS.clone();
    }

    public void setGLOBALPARAMS(BitSet GLOBALPARAMS) {
        this.GLOBALPARAMS = GLOBALPARAMS;
    }

    public List<Integer> getFITNESS() {
        return FITNESS;
    }

    public void setFITNESS(List<Integer> FITNESS) {
        this.FITNESS = FITNESS;
    }

    public int[] getTRIAL() {
        return TRIAL;
    }

    public void setTRIAL(int[] TRIAL) {
        this.TRIAL = TRIAL;
    }

    public List<Double> getPROB() {
        return new ArrayList<>(PROB);
    }

    public void setPROB(List<Double> PROB) {
        this.PROB = PROB;
    }

    public List<Tuple2<Integer, Double>> getPROBSRW() {
        return new ArrayList<>(PROBSRW);
    }

    public void setPROBSRW(List<Tuple2<Integer, Double>> PROBSRW) {
        this.PROBSRW = PROBSRW;
    }

    public Integer getGLOBALMIN() {
        return GLOBALMIN;
    }

    public void setGLOBALMIN(Integer GLOBALMIN) {
        this.GLOBALMIN = GLOBALMIN;
    }

    public List<Integer> getGLOBALMINS() {
        return GLOBALMINS;
    }

    public void setGLOBALMINS(List<Integer> GLOBALMINS) {
        this.GLOBALMINS = GLOBALMINS;
    }

    public Double getMEAN() {
        return MEAN;
    }

    public void setMEAN(Double MEAN) {
        this.MEAN = MEAN;
    }

    public Random getRANDOM() {
        return RANDOM;
    }

    public void setRANDOM(Random RANDOM) {
        this.RANDOM = RANDOM;
    }

    public long getSEED() {
        return SEED;
    }

    public void setSEED(long SEED) {
        this.SEED = SEED;
    }


    // Custom Methods
    public void addFoodSource(BitSet foodSource) {
        this.FOODS.add(foodSource);
    }

    public void addFitness(Integer fitness) {
        this.FITNESS.add(fitness);
    }

    public void setTrial(int index, int value) {
        this.TRIAL[index] = value;
    }

    public Integer getFitness(int i) {
        return this.FITNESS.get(i);
    }

    public BitSet getFoodSource(int i) {
        return (BitSet) this.FOODS.get(i).clone();
    }

    public double getProbability(int i) {
        return this.PROB.get(i);
    }

    public int getTrial(int i) {
        return this.TRIAL[i];
    }

    public void setFoodSource(int i, BitSet foodSource) {
        this.FOODS.set(i, (BitSet) foodSource.clone());
    }

    public void setFitness(int i, Integer value) {
        this.FITNESS.set(i, value);
    }

    public void incrementTrial(int i) {
        this.TRIAL[i]++;
    }

    public void setProbability(int index, double value) {
        this.PROB.set(index, value);
    }

    public boolean getGlobalParamsColumnValue(int j) {
        return this.GLOBALPARAMS.get(j);
    }

    public void addGlobalMin(Integer value) {
        this.GLOBALMINS.add(value);
    }

    public double getProbabilityValue(int index) {
        Tuple2<Integer, Double> prob = PROBSRW.get(index);
        return prob.getT2();
    }

    public int getProbabilityIndex(int index) {
        Tuple2<Integer, Double> prob = PROBSRW.get(index);
        return prob.getT1();
    }
}
