
package com.bear.core.util;

import java.util.Locale;


public final class EnglishEnums {

    private EnglishEnums() {
    }


    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
        return valueOf(enumType, name, null);
    }


    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name, final T defaultValue) {
        return name == null ? defaultValue : Enum.valueOf(enumType, name.toUpperCase(Locale.ENGLISH));
    }

}
