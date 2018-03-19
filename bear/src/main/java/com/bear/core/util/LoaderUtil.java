
package com.bear.core.util;

public final class LoaderUtil {
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        return  LoaderUtil.class.getClassLoader().loadClass(className);
    }
}
