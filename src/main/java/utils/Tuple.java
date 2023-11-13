package main.java.utils;

public class Tuple {
    private Integer t1;
    private Double t2;
    private Integer t3;

    public Tuple(Integer t1, Double t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public Tuple(Integer t1, Double t2, Integer t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public Integer getT1() {
        return t1;
    }

    public void setT1(Integer t1) {
        this.t1 = t1;
    }

    public Double getT2() {
        return t2;
    }

    public void setT2(Double t2) {
        this.t2 = t2;
    }

    public Integer getT3() {
        return t3;
    }

    public void setT3(Integer t3) {
        this.t3 = t3;
    }
}
