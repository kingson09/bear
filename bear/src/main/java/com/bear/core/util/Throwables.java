
package com.bear.core.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public final class Throwables {

    private Throwables() {
    }


    public static Throwable getRootCause(final Throwable throwable) {
        Throwable cause;
        Throwable root = throwable;
        while ((cause = root.getCause()) != null) {
            root = cause;
        }
        return root;
    }


    public static List<String> toStringList(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
        } catch (final RuntimeException ex) {
            // Ignore any exceptions.
        }
        pw.flush();
        final List<String> lines = new ArrayList<>();
        final LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (final IOException ex) {
            if (ex instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            lines.add(ex.toString());
        } finally {
            Closer.closeSilently(reader);
        }
        return lines;
    }


    public static void rethrow(final Throwable t) {
        Throwables.<RuntimeException>rethrow0(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void rethrow0(final Throwable t) throws T {
        throw (T) t;
    }
}
