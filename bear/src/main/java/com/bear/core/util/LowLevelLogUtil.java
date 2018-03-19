

package com.bear.core.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;


final class LowLevelLogUtil {

    private static PrintWriter writer = new PrintWriter(System.err, true);

    public static void recordeException(final Throwable exception) {
        exception.printStackTrace(writer);
    }

    public static void recordeException(final String message, final Throwable exception) {
        if (message != null) {
            writer.println(message);
        }
        recordeException(exception);
    }


    public static void setOutputStream(final OutputStream out) {
        LowLevelLogUtil.writer = new PrintWriter(Objects.requireNonNull(out), true);
    }


    public static void setWriter(final Writer writer) {
        LowLevelLogUtil.writer = new PrintWriter(Objects.requireNonNull(writer), true);
    }

    private LowLevelLogUtil() {
    }
}
