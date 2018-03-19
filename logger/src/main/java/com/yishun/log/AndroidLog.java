package com.yishun.log;

import android.content.Context;

import com.bear.core.Event;
import com.bear.core.Recorder;
import com.bear.core.RecorderContext;
import com.bear.core.RecorderManager;
import com.bear.core.command.CommandEvent;
import com.bear.core.config.Configuration;
import com.bear.core.config.RecorderConfig;
import com.bear.core.lookup.StrLookup;
import com.bear.core.util.ReflectionUtil;
import com.yishun.log.appender.LogcatAppender;
import com.yishun.log.event.DefaultEventFactory;
import com.yishun.log.filter.Level;


public class AndroidLog {
  private static final String FQCN = AndroidLog.class.getName();
  private static final String RECORDER_NAME = "logger";
  public static final Level DEFAULT_LEVEL = Level.TRACE;
  private static Level level = DEFAULT_LEVEL;
  private static RecorderContext recorderContext;
  private static Recorder recorder;
  private final String name;

  public AndroidLog(final String name) {
    this.name = name;
  }

  public static boolean init(Context context, StrLookup lookup) {
    if (recorder == null) {
      recorderContext = RecorderManager.getRecorderContext();
      if (recorderContext == null) {
        Configuration configuration = DefaultXMLConfigureFactory.getConfiguration(context);
        if (lookup != null) {
          configuration.getStrSubstitutor().setVariableResolver(lookup);
        }
        RecorderManager.initialize(configuration);
        recorderContext = RecorderManager.getRecorderContext();
      }
      if (recorderContext.getConfiguration().getRecorderConfig(RECORDER_NAME) == null) {
        final RecorderConfig defaultRecorderConfig = new RecorderConfig(AndroidLog.getRecorderName(), false);
        defaultRecorderConfig.addAppender(LogcatAppender.createAppender("logcat", false, null, null));
        recorderContext.getConfiguration().addRecorder(RECORDER_NAME, defaultRecorderConfig);
      }
      recorder = recorderContext.getRecorder(RECORDER_NAME);
    }
    if (recorder != null) {
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          shutdown();
        }
      }));
      Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerForLogger(context));
      return true;
    }
    return false;
  }

  public static AndroidLog getLogger() {
    return getLogger(ReflectionUtil.getCallerClass(2));
  }

  public static AndroidLog getLogger(final Class<?> clazz) {
    if (clazz == null) {
      final Class<?> candidate = ReflectionUtil.getCallerClass(2);
      if (candidate == null) {
        throw new UnsupportedOperationException("No class provided, and an appropriate one cannot be found.");
      }
      return getLogger(candidate);
    }
    return getLogger(clazz.getSimpleName());
  }

  public static AndroidLog getLogger(String name) {
    return new AndroidLog(name);
  }

  public static String getRecorderName() {
    return RECORDER_NAME;
  }

  public static void setLogLevel(Level newLevel) {
    if (recorder == null) {
      return;
    }
    level = newLevel;
    recorder.record(new CommandEvent("set_level", null, "logLevel", level));
  }

  public static void shutdown() {
    if (recorder == null) {
      return;
    }
    recorder.record(new CommandEvent("shutdown", true, null));
  }

  public static Level getLogLevel() {
    return level;
  }

  public static boolean isEnabled(Event e) {
    return !recorderContext.getConfiguration().isFiltered(e);
  }

  public static void logIfEnabled(Event e) {
    if (isEnabled(e)) {
      recorder.record(e);
    }
  }

  public static void trace(final String tag, final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(AndroidLog.class.getName(), RECORDER_NAME, tag, msg, Level.TRACE));
  }

  public static void debug(final String tag, final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, tag, msg, Level.DEBUG));
  }

  public static void info(final String tag, final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, tag, msg, Level.INFO));
  }

  public static void warn(final String tag, final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, tag, msg, Level.WARN));
  }

  public static void error(final String tag, final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, tag, msg, Level.ERROR));
  }

  public void trace(final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(AndroidLog.class.getName(), RECORDER_NAME, name, msg, Level.TRACE));
  }

  public void debug(final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, name, msg, Level.DEBUG));
  }

  public void info(final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, name, msg, Level.INFO));
  }

  public void warn(final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, name, msg, Level.WARN));
  }

  public void error(final String msg) {
    if (recorder == null) {
      return;
    }
    logIfEnabled(DefaultEventFactory.createEvent(FQCN, RECORDER_NAME, name, msg, Level.ERROR));
  }
}
