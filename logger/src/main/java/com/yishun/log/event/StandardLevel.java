package com.yishun.log.event;

import java.util.EnumSet;



public enum StandardLevel {


  OFF(0),


  FATAL(100),


  ERROR(200),


  WARN(300),


  INFO(400),


  DEBUG(500),


  TRACE(600),


  ALL(Integer.MAX_VALUE);


  private final int intLevel;

  private static final EnumSet<StandardLevel> levelSet = EnumSet.allOf(StandardLevel.class);

  private StandardLevel(final int val) {
    intLevel = val;
  }


  public int intLevel() {
    return intLevel;
  }


  public static StandardLevel getStandardLevel(final int intLevel) {
    StandardLevel level = StandardLevel.OFF;
    for (final StandardLevel lvl : levelSet) {
      if (lvl.intLevel() > intLevel) {
        break;
      }
      level = lvl;
    }
    return level;
  }
}