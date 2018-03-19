package com.yishun.log.filter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.yishun.log.event.StandardLevel;


public final class Level implements Comparable<Level>, Serializable {

  private static final long serialVersionUID = 1581082L;
  private static final ConcurrentMap<String, Level> levels = new ConcurrentHashMap<String, Level>();


  public static final Level OFF;


  public static final Level FATAL;


  public static final Level ERROR;


  public static final Level WARN;


  public static final Level INFO;


  public static final Level DEBUG;


  public static final Level TRACE;


  public static final Level ALL;

  static {
    OFF = new Level("OFF", StandardLevel.OFF.intLevel());
    FATAL = new Level("FATAL", StandardLevel.FATAL.intLevel());
    ERROR = new Level("ERROR", StandardLevel.ERROR.intLevel());
    WARN = new Level("WARN", StandardLevel.WARN.intLevel());
    INFO = new Level("INFO", StandardLevel.INFO.intLevel());
    DEBUG = new Level("DEBUG", StandardLevel.DEBUG.intLevel());
    TRACE = new Level("TRACE", StandardLevel.TRACE.intLevel());
    ALL = new Level("ALL", StandardLevel.ALL.intLevel());
  }


  public static final String CATEGORY = "Level";

  private final String name;
  private final int intLevel;
  private final StandardLevel standardLevel;

  private Level(final String name, final int intLevel) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Illegal null Level constant");
    }
    if (intLevel < 0) {
      throw new IllegalArgumentException("Illegal Level int less than zero.");
    }
    this.name = name;
    this.intLevel = intLevel;
    this.standardLevel = StandardLevel.getStandardLevel(intLevel);
    if (levels.putIfAbsent(name, this) != null) {
      throw new IllegalStateException("Level " + name + " has already been defined.");
    }
  }


  public int intLevel() {
    return this.intLevel;
  }


  public StandardLevel getStandardLevel() {
    return standardLevel;
  }


  public boolean isLessSpecificThan(final Level level) {
    return this.intLevel >= level.intLevel;
  }


  public boolean isMoreSpecificThan(final Level level) {
    return this.intLevel <= level.intLevel;
  }

  @Override
  @SuppressWarnings("CloneDoesntCallSuperClone")
  public Level clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @Override
  public int compareTo(final Level other) {
    return intLevel < other.intLevel ? -1 : (intLevel > other.intLevel ? 1 : 0);
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof Level && other == this;
  }

  public Class<Level> getDeclaringClass() {
    return Level.class;
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }


  public String name() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }


  public static Level forName(final String name, final int intValue) {
    final Level level = levels.get(name);
    if (level != null) {
      return level;
    }
    try {
      return new Level(name, intValue);
    } catch (final IllegalStateException ex) {
      // The level was added by something else so just return that one.
      return levels.get(name);
    }
  }


  public static Level getLevel(final String name) {
    return levels.get(name);
  }


  public static Level toLevel(final String sArg) {
    return toLevel(sArg, Level.DEBUG);
  }


  public static Level toLevel(final String name, final Level defaultLevel) {
    if (name == null) {
      return defaultLevel;
    }
    final Level level = levels.get(name.toUpperCase(Locale.ENGLISH));
    return level == null ? defaultLevel : level;
  }


  public static Level[] values() {
    final Collection<Level> values = Level.levels.values();
    return values.toArray(new Level[values.size()]);
  }


  public static Level valueOf(final String name) {
    if (name == null) {
      throw new NullPointerException("No level name given.");
    }
    final String levelName = name.toUpperCase(Locale.ENGLISH);
    if (levels.containsKey(levelName)) {
      return levels.get(levelName);
    }
    throw new IllegalArgumentException("Unknown level constant [" + levelName + "].");
  }


  public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
    return Enum.valueOf(enumType, name);
  }

  // for deserialization
  protected Object readResolve() {
    return Level.valueOf(this.name);
  }
}