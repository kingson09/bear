package com.bear.core.lookup;

import java.util.Arrays;

import com.bear.core.util.Chars;
import com.bear.core.util.Strings;



public abstract class StrMatcher {


  private static final StrMatcher COMMA_MATCHER = new CharMatcher(',');

  private static final StrMatcher TAB_MATCHER = new CharMatcher(Chars.TAB);

  private static final StrMatcher SPACE_MATCHER = new CharMatcher(Chars.SPACE);

  private static final StrMatcher SPLIT_MATCHER = new CharSetMatcher(" \t\n\r\f".toCharArray());

  private static final StrMatcher TRIM_MATCHER = new TrimMatcher();

  private static final StrMatcher SINGLE_QUOTE_MATCHER = new CharMatcher(Chars.QUOTE);

  private static final StrMatcher DOUBLE_QUOTE_MATCHER = new CharMatcher(Chars.DQUOTE);

  private static final StrMatcher QUOTE_MATCHER = new CharSetMatcher("'\"".toCharArray());

  private static final StrMatcher NONE_MATCHER = new NoMatcher();


  protected StrMatcher() {
  }


  public static StrMatcher commaMatcher() {
    return COMMA_MATCHER;
  }


  public static StrMatcher tabMatcher() {
    return TAB_MATCHER;
  }


  public static StrMatcher spaceMatcher() {
    return SPACE_MATCHER;
  }


  public static StrMatcher splitMatcher() {
    return SPLIT_MATCHER;
  }


  public static StrMatcher trimMatcher() {
    return TRIM_MATCHER;
  }


  public static StrMatcher singleQuoteMatcher() {
    return SINGLE_QUOTE_MATCHER;
  }


  public static StrMatcher doubleQuoteMatcher() {
    return DOUBLE_QUOTE_MATCHER;
  }


  public static StrMatcher quoteMatcher() {
    return QUOTE_MATCHER;
  }


  public static StrMatcher noneMatcher() {
    return NONE_MATCHER;
  }


  public static StrMatcher charMatcher(final char ch) {
    return new CharMatcher(ch);
  }


  public static StrMatcher charSetMatcher(final char[] chars) {
    if (chars == null || chars.length == 0) {
      return NONE_MATCHER;
    }
    if (chars.length == 1) {
      return new CharMatcher(chars[0]);
    }
    return new CharSetMatcher(chars);
  }


  public static StrMatcher charSetMatcher(final String chars) {
    if (Strings.isEmpty(chars)) {
      return NONE_MATCHER;
    }
    if (chars.length() == 1) {
      return new CharMatcher(chars.charAt(0));
    }
    return new CharSetMatcher(chars.toCharArray());
  }


  public static StrMatcher stringMatcher(final String str) {
    if (Strings.isEmpty(str)) {
      return NONE_MATCHER;
    }
    return new StringMatcher(str);
  }


  public abstract int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd);


  public int isMatch(final char[] buffer, final int pos) {
    return isMatch(buffer, pos, 0, buffer.length);
  }

  //-----------------------------------------------------------------------


  static final class CharSetMatcher extends StrMatcher {

    private final char[] chars;


    CharSetMatcher(final char[] chars) {
      super();
      this.chars = chars.clone();
      Arrays.sort(this.chars);
    }


    @Override
    public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
      return Arrays.binarySearch(chars, buffer[pos]) >= 0 ? 1 : 0;
    }
  }

  //-----------------------------------------------------------------------


  static final class CharMatcher extends StrMatcher {

    private final char ch;


    CharMatcher(final char ch) {
      super();
      this.ch = ch;
    }


    @Override
    public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
      return ch == buffer[pos] ? 1 : 0;
    }
  }

  //-----------------------------------------------------------------------


  static final class StringMatcher extends StrMatcher {

    private final char[] chars;


    StringMatcher(final String str) {
      super();
      chars = str.toCharArray();
    }


    @Override
    public int isMatch(final char[] buffer, int pos, final int bufferStart, final int bufferEnd) {
      final int len = chars.length;
      if (pos + len > bufferEnd) {
        return 0;
      }
      for (int i = 0; i < chars.length; i++, pos++) {
        if (chars[i] != buffer[pos]) {
          return 0;
        }
      }
      return len;
    }
  }

  //-----------------------------------------------------------------------


  static final class NoMatcher extends StrMatcher {


    NoMatcher() {
      super();
    }


    @Override
    public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
      return 0;
    }
  }

  //-----------------------------------------------------------------------


  static final class TrimMatcher extends StrMatcher {


    TrimMatcher() {
      super();
    }


    @Override
    public int isMatch(final char[] buffer, final int pos, final int bufferStart, final int bufferEnd) {
      return buffer[pos] <= ' ' ? 1 : 0;
    }
  }

}

