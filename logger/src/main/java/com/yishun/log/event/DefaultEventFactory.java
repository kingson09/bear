package com.yishun.log.event;

import com.bear.core.Event;
import com.yishun.log.filter.Level;
import com.yishun.log.util.TestClock;


public class DefaultEventFactory {

  private static TestClock clock = TestClock.getClock("EventFactory");
  private static final DefaultEventFactory instance = new DefaultEventFactory();

  public static DefaultEventFactory getInstance() {
    return instance;
  }

  public static Event createEvent(final String loggerFqcn, final String loggerName, final String tag,
      final String message, Level level) {
    clock.start();
    LogEvent.Builder builder = LogEvent.newBuilder();
    builder.setLoggerName(loggerName);
    builder.setLoggerFqcn(loggerFqcn);
    builder.setTag(tag);
    builder.setLevel(level);
    builder.setMessage(message);
    builder.setThreadName(Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
    builder.setIncludeLocation(false);
    //builder.setSource(LogEvent.calcLocation(loggerFqcn));
    LogEvent r = builder.build();
    clock.stop();
    return r;
  }
}
