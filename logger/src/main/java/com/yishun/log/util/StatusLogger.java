package com.yishun.log.util;

import java.text.Format;
import java.text.MessageFormat;

import android.util.Log;


public class StatusLogger {
  private static String TAG = "logger";
  public final static int LEVEL_ERROR = 0;
  public final static int LEVEL_WARN = 1;
  public final static int LEVEL_INFO = 2;
  public final static int LEVEL_DEBUG = 3;
  public final static int LEVEL_VERBOSE = 4;

  public static int level = -4;
  private static final StatusLogger STATUS_LOGGER = new StatusLogger();

  public static StatusLogger getLogger() {
    return STATUS_LOGGER;
  }

  public void warn(final String message, final Object... params) {
    if (level >= LEVEL_WARN) {
      Log.w(TAG, formatMessage(message, params));
    }
  }

  public void warn(final String message) {
    if (level >= LEVEL_WARN) {
      Log.w(TAG, message);
    }
  }

  public void debug(final String message, final Object... params) {
    if (level >= LEVEL_DEBUG) {
      Log.d(TAG, formatMessage(message, params));
    }
  }

  public void debug(final String message) {
    if (level >= LEVEL_DEBUG) {
      Log.d(TAG, message);
    }
  }

  public void error(final String message, final Object... params) {
    if (level >= LEVEL_ERROR) {
      Log.e(TAG, formatMessage(message, params));
    }

  }

  private String formatMessage(String msgPattern, final Object... params) {
    try {
      final MessageFormat format = new MessageFormat(msgPattern);
      final Format[] formats = format.getFormats();
      if (formats != null && formats.length > 0) {
        return MessageFormat.format(msgPattern, params);
      }
    } catch (final Exception ignored) {

    }
    return "";
  }
}
