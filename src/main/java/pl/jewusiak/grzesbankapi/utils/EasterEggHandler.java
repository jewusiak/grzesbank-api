package pl.jewusiak.grzesbankapi.utils;

import java.util.Random;

public class EasterEggHandler {
    public static String returnRandomSeqNumber() {
        int n = new Random().nextInt(1, 100);
        return n + getSuffix(n);
    }

    private static String getSuffix(int n) {
        if (n == 1 || n > 11 && n % 10 == 1) return "st";
        if (n == 2 || n > 12 && n % 10 == 2) return "nd";
        if (n == 3 || n > 13 && n % 10 == 3) return "rd";
        return "th";
    }
}
