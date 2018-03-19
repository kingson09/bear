package com.bear.core.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.util.ObjectArrayIterator;


public final class CompositeFilter implements Iterable<Filter>, Filter {

  private static final Filter[] EMPTY_FILTERS = new Filter[0];
  private final Filter[] filters;

  private CompositeFilter() {
    this.filters = EMPTY_FILTERS;
  }

  private CompositeFilter(final Filter[] filters) {
    this.filters = filters == null ? EMPTY_FILTERS : filters;
  }

  public CompositeFilter addFilter(final Filter filter) {
    if (filter == null) {
      // null does nothing
      return this;
    }
    if (filter instanceof CompositeFilter) {
      final int size = this.filters.length + ((CompositeFilter) filter).size();
      final Filter[] copy = Arrays.copyOf(this.filters, size);
      final int index = this.filters.length;
      for (final Filter currentFilter : ((CompositeFilter) filter).filters) {
        copy[index] = currentFilter;
      }
      return new CompositeFilter(copy);
    }
    final Filter[] copy = Arrays.copyOf(this.filters, this.filters.length + 1);
    copy[this.filters.length] = filter;
    return new CompositeFilter(copy);
  }

  public CompositeFilter removeFilter(final Filter filter) {
    if (filter == null) {
      return this;
    }
    final List<Filter> filterList = new ArrayList<>(Arrays.asList(this.filters));
    if (filter instanceof CompositeFilter) {
      for (final Filter currentFilter : ((CompositeFilter) filter).filters) {
        filterList.remove(currentFilter);
      }
    } else {
      filterList.remove(filter);
    }
    return new CompositeFilter(filterList.toArray(new Filter[this.filters.length - 1]));
  }

  @Override
  public Iterator<Filter> iterator() {
    return new ObjectArrayIterator<>(filters);
  }


  @Deprecated
  public List<Filter> getFilters() {
    return Arrays.asList(filters);
  }

  public Filter[] getFiltersArray() {
    return filters;
  }


  public boolean isEmpty() {
    return this.filters.length == 0;
  }

  public int size() {
    return filters.length;
  }


  @Override
  public Result getOnMismatch() {
    return Result.NEUTRAL;
  }


  @Override
  public Result getOnMatch() {
    return Result.NEUTRAL;
  }


  @Override
  public Result filter(final Event event) {
    Result result = Result.NEUTRAL;
    for (int i = 0; i < filters.length; i++) {
      result = filters[i].filter(event);
      if (result == Result.ACCEPT || result == Result.DENY) {
        return result;
      }
    }
    return result;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filters.length; i++) {
      if (sb.length() == 0) {
        sb.append('{');
      } else {
        sb.append(", ");
      }
      sb.append(filters[i].toString());
    }
    if (sb.length() > 0) {
      sb.append('}');
    }
    return sb.toString();
  }


  public static CompositeFilter createFilters(final Filter[] filters) {
    return new CompositeFilter(filters);
  }

}
