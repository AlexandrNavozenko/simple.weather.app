package com.mentor.simpleweatherapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Util {

    private Util() {

    }

    public static void sleepSecond(final int second) {
        try {
            int index = ThreadLocalRandom.current().nextInt(second) + 1;
            TimeUnit.SECONDS.sleep(index);
            System.out.println("thread name: " + Thread.currentThread().getName() + ", sleep: " + index + "s");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double round(double value) {
        return round(value, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("places must be a positive number");
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}
