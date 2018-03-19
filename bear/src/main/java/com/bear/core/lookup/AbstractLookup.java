package com.bear.core.lookup;



public abstract class AbstractLookup implements StrLookup {


  @Override
  public String lookup(final String key) {
    return lookup(null, key);
  }

}
