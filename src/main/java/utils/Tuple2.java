package main.java.utils;

public class Tuple2<K, V> {
    private K t1;
    private V t2;

    public Tuple2() {
    }

    public Tuple2(K t1, V t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public K getT1() {
        return t1;
    }

    public void setT1(K t1) {
        this.t1 = t1;
    }

    public V getT2() {
        return t2;
    }

    public void setT2(V t2) {
        this.t2 = t2;
    }
}