package com.bear.core.filter;

import com.bear.core.Event;
import com.bear.core.Filter;



public abstract class AbstractFilter implements Filter {

  private final String name;


  protected final Result onMatch;


  protected final Result onMismatch;


  protected AbstractFilter() {
    this(null, null, null);
  }


  protected AbstractFilter(final String name, final Result onMatch, final Result onMismatch) {
    this.name = name;
    this.onMatch = onMatch == null ? Result.NEUTRAL : onMatch;
    this.onMismatch = onMismatch == null ? Result.DENY : onMismatch;
  }

  @Override
  public String getName() {
    return name;
  }

  protected boolean equalsImpl(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AbstractFilter other = (AbstractFilter) obj;
    if (onMatch != other.onMatch) {
      return false;
    }
    if (onMismatch != other.onMismatch) {
      return false;
    }
    return true;
  }


  @Override
  public Result filter(final Event event) {
    return Result.NEUTRAL;
  }


  @Override
  public final Result getOnMatch() {
    return onMatch;
  }


  @Override
  public final Result getOnMismatch() {
    return onMismatch;
  }

  protected int hashCodeImpl() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((onMatch == null) ? 0 : onMatch.hashCode());
    result = prime * result + ((onMismatch == null) ? 0 : onMismatch.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
