package com.bear.core.config;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bear.core.Appender;
import com.bear.core.Filter;
import com.bear.core.Recorder;
import com.bear.core.filter.AbstractFilterable;
import com.bear.core.lookup.StrSubstitutor;
import com.bear.core.util.StatusLogger;




public abstract class AbstractConfiguration extends AbstractFilterable implements Configuration {

  protected static final StatusLogger LOGGER = StatusLogger.getLogger();

  private String name;
  private ConcurrentMap<String, RecorderConfig> recorderConfigs = new ConcurrentHashMap<>();
  private StrSubstitutor subst=new StrSubstitutor();


  protected AbstractConfiguration() {
  }

  public void initialize() {
    setup();
    doConfigure();
    super.initialize();
  }

  public void setup() {
    // default does nothing, subclasses do work.
  }

  protected abstract void doConfigure();

  //TODO 增加默认情况下的日志处理
  protected void setToDefault() {
    // LOG4J2-1176 facilitate memory leak investigation
    //    setName(Log4aConfiguration.DEFAULT_NAME + "@" + Integer.toHexString(hashCode()));
    //    final Layout<? extends Serializable> layout =
    //        PatternLayout.newBuilder().withPattern(Log4aConfiguration.DEFAULT_PATTERN).withConfiguration(this).build();
    //    final Appender appender = ConsoleAppender.createDefaultAppenderForLayout(layout);
    //    appender.start();
    //    addAppender(appender);
    //    final RecorderConfig rootRecorderConfig = getRootRecorder();
    //    rootRecorderConfig.addAppender(appender, null, null);
    //
    //    final Level defaultLevel = Level.ERROR;
    //    final String levelName =
    //        PropertiesUtil.getProperties().getStringProperty(Log4aConfiguration.DEFAULT_LEVEL, defaultLevel.name());
    //    final Level level = Level.valueOf(levelName);
    //    rootRecorderConfig.setLevel(level != null ? level : defaultLevel);
  }


  public void setName(final String name) {
    this.name = name;
  }


  @Override
  public String getName() {
    return name;
  }



  @Override
  public synchronized void addRecorderAppender(final Recorder recorder, final Appender appender) {
    final String recorderName = recorder.getName();
    final RecorderConfig lc = getRecorderConfig(recorderName);
    if (lc.getName().equals(recorderName)) {
      lc.addAppender(appender);
    }
  }


  @Override
  public synchronized void addRecorderFilter(final Recorder recorder, final Filter filter) {
    final String recorderName = recorder.getName();
    final RecorderConfig lc = getRecorderConfig(recorderName);
    if (lc.getName().equals(recorderName)) {
      lc.addFilter(filter);
    }
  }


  public synchronized void removeAppender(final String appenderName) {
    for (final RecorderConfig recorder : recorderConfigs.values()) {
      recorder.removeAppender(appenderName);
    }
  }



  @Override
  public RecorderConfig getRecorderConfig(final String recorderName) {
    RecorderConfig recorderConfig = recorderConfigs.get(recorderName);
    if (recorderConfig != null) {
      return recorderConfig;
    }
    String substr = recorderName;
      recorderConfig = recorderConfigs.get(recorderName);
      if (recorderConfig != null) {
        return recorderConfig;
      }
    return null;
  }


  @Override
  public Map<String, RecorderConfig> getRecorders() {
    return Collections.unmodifiableMap(recorderConfigs);
  }


  public RecorderConfig getRecorder(final String recorderName) {
    return recorderConfigs.get(recorderName);
  }


  @Override
  public synchronized void addRecorder(final String recorderName, final RecorderConfig recorderConfig) {
    recorderConfigs.putIfAbsent(recorderName, recorderConfig);
  }


  @Override
  public synchronized void removeRecorder(final String recorderName) {
    recorderConfigs.remove(recorderName);
  }

  @Override
  public StrSubstitutor getStrSubstitutor() {
    return subst;
  }
}
