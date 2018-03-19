
package com.bear.core.util;

public final class Strings {

    public static final String EMPTY = "";

    private Strings() {

    }


    public static boolean isBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }


    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }


    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }


    public static String trimToNull(final String str) {
        final String ts = str == null ? null : str.trim();
        return isEmpty(ts) ? null : ts;
    }

}
