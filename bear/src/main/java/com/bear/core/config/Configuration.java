package com.bear.core.config;




import java.util.Map;

import com.bear.core.Appender;
import com.bear.core.Filter;
import com.bear.core.LifeCycle;
import com.bear.core.Recorder;
import com.bear.core.filter.Filterable;
import com.bear.core.lookup.StrSubstitutor;


public interface Configuration extends Filterable, LifeCycle {

  String CONTEXT_PROPERTIES = "ContextProperties";


  String getName();


  RecorderConfig getRecorderConfig(String name);


  Map<String, RecorderConfig> getRecorders();

  void addRecorderAppender(Recorder recorder, Appender appender);

  void addRecorderFilter(Recorder recorder, Filter filter);

  void addRecorder(final String name, final RecorderConfig recorderConfig);

  void removeRecorder(final String name);

  StrSubstitutor getStrSubstitutor();

}
