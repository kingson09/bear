package com.yishun.log;

import com.bear.core.Filter;
import com.bear.core.config.AbstractConfiguration;
import com.bear.core.config.RecorderConfig;
import com.yishun.log.appender.LogcatAppender;
import com.yishun.log.filter.Level;
import com.yishun.log.filter.LevelFilter;

/**
 * Created by bjliuzhanyong on 2017/8/10.
 */

public class DefaultConfigurationImpl extends AbstractConfiguration {

  @Override
  protected void doConfigure() {

    final RecorderConfig defaultRecorderConfig = new RecorderConfig(AndroidLog.getRecorderName(), false);
    LevelFilter levelAppender = new LevelFilter("level", Level.DEBUG, Filter.Result.ACCEPT, Filter.Result.DENY);
    defaultRecorderConfig.addAppender(LogcatAppender.createAppender("logcat", false, null, levelAppender));
    addRecorder(AndroidLog.getRecorderName(), defaultRecorderConfig);
  }
}
