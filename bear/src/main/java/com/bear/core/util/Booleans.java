
package com.bear.core.util;


public final class Booleans {

    private Booleans() {
    }


    public static boolean parseBoolean(final String s, final boolean defaultValue) {
        return "true".equalsIgnoreCase(s) || (defaultValue && !"false".equalsIgnoreCase(s));
    }

}
