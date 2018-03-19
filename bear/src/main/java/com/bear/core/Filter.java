package com.bear.core;

import com.bear.core.util.EnglishEnums;



public interface Filter {

  String ELEMENT_TYPE = "filter";


  enum Result {

    ACCEPT,

    NEUTRAL,

    DENY;


    public static Result toResult(final String name) {
      return toResult(name, null);
    }


    public static Result toResult(final String name, final Result defaultResult) {
      return EnglishEnums.valueOf(Result.class, name, defaultResult);
    }
  }


  Result getOnMismatch();


  Result getOnMatch();



  Result filter(Event event);

  String getName();
}
