
package com.bear.core.util;


public final class Chars {


    public static final char CR = '\r';


    public static final char DQUOTE = '\"';


    public static final char EQ = '=';


    public static final char LF = '\n';


    public static final char QUOTE = '\'';


    public static final char SPACE = ' ';


    public static final char TAB = '\t';


    public static char getUpperCaseHex(final int digit) {
        if (digit < 0 || digit >= 16) {
            return '\0';
        }
        return digit < 10 ? getNumericalDigit(digit) : getUpperCaseAlphaDigit(digit);
    }


    public static char getLowerCaseHex(final int digit) {
        if (digit < 0 || digit >= 16) {
            return '\0';
        }
        return digit < 10 ? getNumericalDigit(digit) : getLowerCaseAlphaDigit(digit);
    }

    private static char getNumericalDigit(final int digit) {
        return (char) ('0' + digit);
    }

    private static char getUpperCaseAlphaDigit(final int digit) {
        return (char) ('A' + digit - 10);
    }

    private static char getLowerCaseAlphaDigit(final int digit) {
        return (char) ('a' + digit - 10);
    }

    private Chars() {
    }
}
