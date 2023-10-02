package main.java.utils;

public class Tuple3<K, V1, V2> {
    private K t1;
    private V1 t2;
    private V2 t3;

    public Tuple3() {
    }

    public Tuple3(K t1, V1 t2, V2 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public K getT1() {
        return t1;
    }

    public void setT1(K t1) {
        this.t1 = t1;
    }

    public V1 getT2() {
        return t2;
    }

    public void setT2(V1 t2) {
        this.t2 = t2;
    }

    public V2 getT3() {
        return t3;
    }

    public void setT3(V2 t3) {
        this.t3 = t3;
    }
}
