package main.java.utils;

import java.util.Random;

public class RandomUtil {

    private static final ThreadLocal<Random> RANDOM_THREAD_LOCAL = ThreadLocal.withInitial(Random::new);

    public static Random threadLocalRandom(long seed) {
        Random random = RANDOM_THREAD_LOCAL.get();
        random.setSeed(seed);
        return random;
    }
}