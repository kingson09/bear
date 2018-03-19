package com.bear.core.config;

import java.io.Serializable;
import java.util.Comparator;

import com.bear.core.util.Objects;


public class OrderComparator implements Comparator<Class<?>>, Serializable {

  private static final long serialVersionUID = 1L;
  private static final Comparator<Class<?>> INSTANCE = new OrderComparator();


  public static Comparator<Class<?>> getInstance() {
    return INSTANCE;
  }

  @Override
  public int compare(final Class<?> lhs, final Class<?> rhs) {
    final Order lhsOrder = Objects.requireNonNull(lhs, "lhs").getAnnotation(Order.class);
    final Order rhsOrder = Objects.requireNonNull(rhs, "rhs").getAnnotation(Order.class);
    if (lhsOrder == null && rhsOrder == null) {
      // both unannotated means equal priority
      return 0;
    }
    // if only one class is @Order-annotated, then prefer that one
    if (rhsOrder == null) {
      return -1;
    }
    if (lhsOrder == null) {
      return 1;
    }
    // larger value means higher priority
    return Integer.signum(rhsOrder.value() - lhsOrder.value());
  }
}
