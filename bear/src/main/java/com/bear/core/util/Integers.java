
package com.bear.core.util;


public final class Integers {

    private static final int BITS_PER_INT = 32;

    private Integers() {
    }


    public static int parseInt(final String s, final int defaultValue) {
        return Strings.isEmpty(s) ? defaultValue : Integer.parseInt(s);
    }


    public static int parseInt(final String s) {
        return parseInt(s, 0);
    }


    public static int ceilingNextPowerOfTwo(final int x) {
        return 1 << (BITS_PER_INT - Integer.numberOfLeadingZeros(x - 1));
    }
}
