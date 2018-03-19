package com.yishun.log.event;

import com.bear.core.Event;
import com.bear.core.util.Clock;
import com.bear.core.util.Strings;
import com.bear.core.util.SystemClock;
import com.yishun.log.filter.Level;


public class LogEvent implements Event {
  private static final Clock clock = new SystemClock();
  private final String loggerName;
  private final String tag;
  private final Level level;
  private final String message;
  private final String threadName;
  private final long timeMillis;
  private final String loggerFqcn;
  private StackTraceElement source;
  private boolean includeLocation = true;

  public static class Builder implements com.bear.core.util.Builder<LogEvent> {

    private String loggerFqcn;
    private Level level;
    private String loggerName;
    private String tag;
    private String message;
    private String threadName;
    private StackTraceElement source;
    private boolean includeLocation;

    public Builder setLoggerFqcn(final String loggerFqcn) {
      this.loggerFqcn = loggerFqcn;
      return this;
    }

    public Builder setLevel(final Level level) {
      this.level = level;
      return this;
    }

    public Builder setLoggerName(final String loggerName) {
      this.loggerName = loggerName;
      return this;
    }

    public Builder setTag(final String tag) {
      this.tag = tag;
      return this;
    }

    public Builder setMessage(final String message) {
      this.message = message;
      return this;
    }

    public Builder setThreadName(final String threadName) {
      this.threadName = threadName;
      return this;
    }

    public Builder setSource(final StackTraceElement source) {
      this.source = source;
      return this;
    }

    public Builder setIncludeLocation(final boolean includeLocation) {
      this.includeLocation = includeLocation;
      return this;
    }

    @Override
    public LogEvent build() {
      return new LogEvent(loggerName, tag, loggerFqcn, level, message, includeLocation, threadName, source);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public LogEvent() {
    this(clock.currentTimeMillis());
  }

  public LogEvent(final long timestamp) {
    this(Strings.EMPTY, Strings.EMPTY, Strings.EMPTY, null, null, true, null, timestamp, Strings.EMPTY);
  }

  public LogEvent(final String loggerName, final String tag, final String loggerFQCN, final Level level,
      final String message, final boolean includeLocation, final String threadName, final StackTraceElement location) {
    this(loggerName, tag, loggerFQCN, level, message, includeLocation, location, clock.currentTimeMillis(), threadName);
  }

  public LogEvent(final String loggerName, final String tag, final String loggerFQCN, final Level level,
      final String message, final boolean includeLocation, final StackTraceElement location, final long timestamp,
      final String threadName) {
    this.loggerName = loggerName;
    this.tag = tag;
    this.loggerFqcn = loggerFQCN;
    this.level = level;
    this.message = message;
    this.source = location;
    this.timeMillis = timestamp;
    this.threadName = threadName;
    this.includeLocation = includeLocation;
  }

  public Level getLevel() {
    return level;
  }

  public String getMessage() {
    return message;
  }

  public String getLoggerName() {
    return loggerName;
  }

  public String getTag() {
    return tag;
  }

  public long getTimeMillis() {
    return timeMillis;
  }

  public String getThreadName() {
    return threadName;
  }

  public String getLoggerFqcn() {
    return loggerFqcn;
  }

  public StackTraceElement getSource() {
    if (source != null) {
      return source;
    }
    if (loggerFqcn == null || !includeLocation) {
      return null;
    }
    source = calcLocation(loggerFqcn);
    return source;
  }

  public static StackTraceElement calcLocation(final String fqcnOfLogger) {
    if (fqcnOfLogger == null) {
      return null;
    }
    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    StackTraceElement last = null;
    for (int i = stackTrace.length - 1; i > 0; i--) {
      final String className = stackTrace[i].getClassName();
      if (fqcnOfLogger.equals(className)) {
        return last;
      }
      last = stackTrace[i];
    }
    return null;
  }

  public boolean isIncludeLocation() {
    return includeLocation;
  }

  public void setIncludeLocation(final boolean includeLocation) {
    this.includeLocation = includeLocation;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    final String n = loggerName.isEmpty() ? "default" : loggerName;
    sb.append("Logger=").append(n);
    sb.append(" Level=").append(level.name());
    sb.append(" Message=").append(message);
    return sb.toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final LogEvent that = (LogEvent) o;

    if (includeLocation != that.includeLocation) {
      return false;
    }
    if (timeMillis != that.timeMillis) {
      return false;
    }
    if (loggerFqcn != null ? !loggerFqcn.equals(that.loggerFqcn) : that.loggerFqcn != null) {
      return false;
    }
    if (level != null ? !level.equals(that.level) : that.level != null) {
      return false;
    }
    if (source != null ? !source.equals(that.source) : that.source != null) {
      return false;
    }

    if (!message.equals(that.message)) {
      return false;
    }
    if (!loggerName.equals(that.loggerName)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = loggerFqcn != null ? loggerFqcn.hashCode() : 0;
    result = 31 * result + (level != null ? level.hashCode() : 0);
    result = 31 * result + loggerName.hashCode();
    result = 31 * result + message.hashCode();
    result = 31 * result + (int) (timeMillis ^ (timeMillis >>> 32));
    result = 31 * result + (source != null ? source.hashCode() : 0);
    result = 31 * result + (includeLocation ? 1 : 0);
    return result;
  }
}
