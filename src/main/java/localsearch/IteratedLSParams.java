package main.java.localsearch;

public class IteratedLSParams {
    private IteratedLSParams(){
    }
    public static final double Pb = 0.5;
    public static final double ALPHA = 0.05d;
    public static final double BETHA = 0.01d;
    public static final double MIN_PENALTY = 0.5;
    public static final double MAX_PENALTY = 1.5;
    public static final int COL_DROP_1 = 12; // TODO best 20
    public static final int COL_DROP_2 = 5; //  TODO best 6
}
