package main.java.utils;

public class Tuple {
    private Integer t1;
    private Double t2;

    public Tuple(Integer t1, Double t2) {
        this.t1 = t1;
        this.t2 = t2;
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
}
