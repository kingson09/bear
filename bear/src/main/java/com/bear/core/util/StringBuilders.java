
package com.bear.core.util;

import java.util.Map.Entry;

import static java.lang.Character.toLowerCase;


public final class StringBuilders {
    private StringBuilders() {
    }


    public static StringBuilder appendDqValue(final StringBuilder sb, final Object value) {
        return sb.append(Chars.DQUOTE).append(value).append(Chars.DQUOTE);
    }


    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final Entry<String, String> entry) {
        return appendKeyDqValue(sb, entry.getKey(), entry.getValue());
    }


    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final String key, final Object value) {
        return sb.append(key).append(Chars.EQ).append(Chars.DQUOTE).append(value).append(Chars.DQUOTE);
    }


    public static void appendValue(final StringBuilder stringBuilder, final Object obj) {
        if (obj == null || obj instanceof String) {
            stringBuilder.append((String) obj);
        } else if (obj instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable) obj).formatTo(stringBuilder);
        } else if (obj instanceof CharSequence) {
            stringBuilder.append((CharSequence) obj);
        } else if (obj instanceof Integer) { // LOG4J2-1437 unbox auto-boxed primitives to avoid calling toString()
            stringBuilder.append(((Integer) obj).intValue());
        } else if (obj instanceof Long) {
            stringBuilder.append(((Long) obj).longValue());
        } else if (obj instanceof Double) {
            stringBuilder.append(((Double) obj).doubleValue());
        } else if (obj instanceof Boolean) {
            stringBuilder.append(((Boolean) obj).booleanValue());
        } else if (obj instanceof Character) {
            stringBuilder.append(((Character) obj).charValue());
        } else if (obj instanceof Short) {
            stringBuilder.append(((Short) obj).shortValue());
        } else if (obj instanceof Float) {
            stringBuilder.append(((Float) obj).floatValue());
        } else {
            stringBuilder.append(obj);
        }
    }


    public static boolean equals(final CharSequence left, final int leftOffset, final int leftLength,
                                    final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (left.charAt(i + leftOffset) != right.charAt(i + rightOffset)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static boolean equalsIgnoreCase(final CharSequence left, final int leftOffset, final int leftLength,
                                              final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (toLowerCase(left.charAt(i + leftOffset)) != toLowerCase(right.charAt(i + rightOffset))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static void trimToMaxSize(final StringBuilder stringBuilder, final int maxSize) {
        if (stringBuilder != null && stringBuilder.length() > maxSize) {
            stringBuilder.setLength(maxSize);
            stringBuilder.trimToSize();
        }
    }
}
