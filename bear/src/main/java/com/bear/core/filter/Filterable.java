package com.bear.core.filter;

import com.bear.core.Event;
import com.bear.core.Filter;


public interface Filterable {


  void addFilter(Filter filter);


  void removeFilter(Filter filter);


  Filter getFilter();


  boolean hasFilter();

  boolean isFiltered(Event event);
}
