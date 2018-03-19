package com.bear.core.config;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.bear.core.Appender;
import com.bear.core.Event;
import com.bear.core.Filter;
import com.bear.core.async.Disruptor;
import com.bear.core.async.EventHandler;
import com.bear.core.command.CommandEvent;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectElement;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.filter.AbstractFilterable;
import com.bear.core.util.Strings;
import com.bear.core.util.TestClock;


public class RecorderConfig extends AbstractFilterable {

  private static final String DEFAULT = "default";
  private static final String COMMAND_SHUTDOWN = "shutdown";
  private static final int MAX_QUEUE_SIZE = 256;
  private static final int MAX_RETRIES = 256;

  private final Map<String, AppenderControl> appenders = new ConcurrentHashMap<String, AppenderControl>();
  private final String name;
  private final boolean async;
  private Disruptor<Event> disruptor;
  private TestClock clock = TestClock.getClock("RecoderConfig");
  private TestClock clockLoggerFilter = TestClock.getClock("LoggerFilter");

  public RecorderConfig(final String name, final boolean async) {
    super(null);
    this.name = name;
    this.async = async;
  }

  protected RecorderConfig(final String name, final boolean async, final List<Appender> appenders,
      final Filter filter) {
    super(filter);
    this.name = name;
    this.async = async;
    if (appenders != null) {
      for (Appender appender : appenders) {
        addAppender(appender);
      }
    }

    final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(MAX_QUEUE_SIZE);
    if (async) {
      disruptor = new Disruptor<Event>(eventQueue);
      disruptor.handleEventsWith(new EventHandler<Event>() {
        @Override
        public void onEvent(Event o) {
          realAccept(o);
        }
      });

      disruptor.start();
    }
  }

  @Override
  public Filter getFilter() {
    return super.getFilter();
  }


  public String getName() {
    return name;
  }


  public void addAppender(final Appender appender) {
    appenders.put(appender.getName(), new AppenderControl(appender));
  }


  public void removeAppender(final String name) {
    appenders.remove(name);
  }


  public Map<String, Appender> getAppenders() {
    final Map<String, Appender> map = new HashMap<String, Appender>();
    for (final Map.Entry<String, AppenderControl> entry : appenders.entrySet()) {
      map.put(entry.getKey(), entry.getValue().getAppender());
    }
    return map;
  }


  public void clearAppenders() {
    final Collection<AppenderControl> controls = appenders.values();
    final Iterator<AppenderControl> iterator = controls.iterator();
    while (iterator.hasNext()) {
      final AppenderControl ctl = iterator.next();
      iterator.remove();
    }
  }


  protected void callAppenders(final Event event) {
    for (final AppenderControl control : appenders.values()) {
      control.callAppender(event);
    }
  }

  public void realRecord(final Event event) {
    clockLoggerFilter.start();
    if (isFiltered(event)) {
      return;
    }
    clockLoggerFilter.stop();
    callAppenders(event);
  }

  public void record(final Event event) {
    if (async && !(event instanceof CommandEvent && ((CommandEvent) event).isSync())) {
      disruptor.publishEvent(event);
    } else {
      realAccept(event);
    }

  }

  public void realAccept(Event event) {
    clock.start();
    if (event instanceof CommandEvent && onCommand((CommandEvent) event)) {
    } else {
      realRecord(event);
    }
    clock.stop();
  }

  private boolean onCommand(CommandEvent event) {
    if (event.getCommand().equals(COMMAND_SHUTDOWN)) {
      shutdown();
      return false;
    }
    return false;
  }

  @Override
  public void shutdown() {
    if (async) {
      disruptor.shutdown(false, 2000L);
    }
    super.shutdown();
  }

  @Override
  public String toString() {
    return Strings.isEmpty(name) ? "default" : name;
  }


  @ObjectFactory
  public static RecorderConfig createRecorder(@ObjectAttribute("name") final String recorderName,
      @ObjectAttribute(value = "async", defaultBoolean = false) final boolean async,
      @ObjectElement("appenders") final Appender[] apps, @ObjectElement("filter") final Filter filter) {
    if (recorderName == null) {
      return null;
    }

    final String name = Strings.isEmpty(recorderName) ? DEFAULT : recorderName;
    List<Appender> appenders = null;
    if (apps != null) {
      appenders = Arrays.asList(apps);
    }
    return new RecorderConfig(name, async, appenders, filter);
  }


}
