package com.bear.core.filter;

import java.util.Iterator;

import com.bear.core.AbstractLifeCycle;
import com.bear.core.Event;
import com.bear.core.Filter;



public abstract class AbstractFilterable extends AbstractLifeCycle implements Filterable {


  public abstract static class Builder<B extends Builder<B>> {

    private Filter filter;

    public Filter getFilter() {
      return filter;
    }

    @SuppressWarnings("unchecked")
    public B asBuilder() {
      return (B) this;
    }

    public B withFilter(final Filter filter) {
      this.filter = filter;
      return asBuilder();
    }

  }


  private volatile Filter filter;

  protected AbstractFilterable(final Filter filter) {
    this.filter = filter;
  }

  protected AbstractFilterable() {
  }


  @Override
  public Filter getFilter() {
    return filter;
  }


  @Override
  public synchronized void addFilter(final Filter filter) {
    if (filter == null) {
      return;
    }
    if (this.filter == null) {
      this.filter = filter;
    } else if (this.filter instanceof CompositeFilter) {
      this.filter = ((CompositeFilter) this.filter).addFilter(filter);
    } else {
      final Filter[] filters = new Filter[] { this.filter, filter };
      this.filter = CompositeFilter.createFilters(filters);
    }
  }


  @Override
  public synchronized void removeFilter(final Filter filter) {
    if (this.filter == null || filter == null) {
      return;
    }
    if (this.filter == filter || this.filter.equals(filter)) {
      this.filter = null;
    } else if (this.filter instanceof CompositeFilter) {
      CompositeFilter composite = (CompositeFilter) this.filter;
      composite = composite.removeFilter(filter);
      if (composite.size() > 1) {
        this.filter = composite;
      } else if (composite.size() == 1) {
        final Iterator<Filter> iter = composite.iterator();
        this.filter = iter.next();
      } else {
        this.filter = null;
      }
    }
  }


  @Override
  public boolean hasFilter() {
    return filter != null;
  }

  @Override
  public boolean isFiltered(final Event event) {
    return filter != null && filter.filter(event) == Filter.Result.DENY;
  }

}



