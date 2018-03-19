package com.yishun.log.filter;

import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.command.CommandEvent;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.filter.AbstractFilter;
import com.yishun.log.event.LogEvent;


public class LevelFilter extends AbstractFilter {

  private final String Command_SET_LEVEL = "set_level";

  private Level level;

  public LevelFilter(final String name, final Level level, final Filter.Result onMatch,
      final Filter.Result onMismatch) {
    super(name, onMatch, onMismatch);
    this.level = level;
  }

  public void setLevel(final Level level) {
    this.level = level;
  }

  public Level getLevel() {
    return this.level;
  }

  @Override
  public Result filter(final Event event) {
    if (event instanceof CommandEvent) {
      if (onCommand((CommandEvent) event)) {
        return Result.DENY;
      } else {
        return Result.ACCEPT;
      }
    }
    return filter(((LogEvent) event).getLevel());
  }

  private boolean onCommand(CommandEvent event) {
    if (event.getCommand().equals(Command_SET_LEVEL)) {
      if (event.getParams() != null && this.getName().equals((String) event.getParams()[0])) {
        this.level = (Level) event.getParams()[1];
        return true;
      }
    }
    return false;
  }

  private Result filter(final Level level) {
    return level.isMoreSpecificThan(this.level) ? onMatch : onMismatch;
  }

  @Override
  public String toString() {
    return level.toString();
  }


  @ObjectFactory
  public static LevelFilter createFilter(@ObjectAttribute("name") final String name,
      @ObjectAttribute("level") final String level, @ObjectAttribute("onMatch") final Result match,
      @ObjectAttribute("onMismatch") final Result mismatch) {
    Level actualLevel;
    try {
      actualLevel = Level.valueOf(level);
    } catch (IllegalArgumentException ex) {
      try {
        actualLevel = Level.forName("level", Integer.parseInt(level));
      } catch (NumberFormatException exp) {
        actualLevel = Level.OFF;
      }

    }
    final Result onMatch = match == null ? Result.NEUTRAL : match;
    final Result onMismatch = mismatch == null ? Result.DENY : mismatch;
    return new LevelFilter(name, actualLevel, onMatch, onMismatch);
  }
}
