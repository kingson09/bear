
package com.bear.core.util;

import java.util.Collection;
import java.util.Map;


public final class Assert {
  private Assert() {
  }


  public static boolean isEmpty(final Object o) {
    if (o == null) {
      return true;
    }
    if (o instanceof CharSequence) {
      return ((CharSequence) o).length() == 0;
    }
    if (o.getClass().isArray()) {
      return ((Object[]) o).length == 0;
    }
    if (o instanceof Collection) {
      return ((Collection<?>) o).isEmpty();
    }
    if (o instanceof Map) {
      return ((Map<?, ?>) o).isEmpty();
    }
    return false;
  }


  public static boolean isNonEmpty(final Object o) {
    return !isEmpty(o);
  }


  public static <T> T requireNonEmpty(final T value) {
    return requireNonEmpty(value, "");
  }


  public static <T> T requireNonEmpty(final T value, final String message) {
    if (isEmpty(value)) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  public static int valueIsAtLeast(final int value, final int minValue) {
    if (value < minValue) {
      throw new IllegalArgumentException("Value should be at least " + minValue + " but was " + value);
    }
    return value;
  }

  public static <T> T requireNonNull(final T object, final String message) {
    if (object == null) {
      throw new NullPointerException(message);
    }
    return object;
  }
}
