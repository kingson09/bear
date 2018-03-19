package com.yishun.log;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.system.ErrnoException;
import android.widget.Toast;

import com.yishun.log.util.StorageUtils;

/**
 * Created by bjliuzhanyong on 2018/1/24.
 */

public class UncaughtExceptionHandlerForLogger implements UncaughtExceptionHandler {
  public static final String KEY_PROTECT_BEAR = "key_protect_bear";//保护日志库
  private Thread.UncaughtExceptionHandler preHandler = null;
  private Context context;

  public UncaughtExceptionHandlerForLogger(Context context) {
    this.context = context;
    preHandler = Thread.getDefaultUncaughtExceptionHandler();
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    if (protectBear(throwable)) {
      AndroidLog.debug(this.getClass().getSimpleName(), throwable.toString());
      AndroidLog.shutdown();
    }
    if (preHandler != null) {
      preHandler.uncaughtException(thread, throwable);
    }
  }

  public boolean protectBear(Throwable t) {
    if (t instanceof IOException || t instanceof ErrnoException) {
      if (StorageUtils.getSDcardFreeSpace() < 50 * 1024 * 1024) {
        Toast.makeText(context, "您的手机磁盘空间不足，请及时清理", Toast.LENGTH_LONG);
        return true;
      }
    }
    StackTraceElement[] elements = t.getStackTrace();
    if (elements != null) {
      for (StackTraceElement element : elements) {
        if (!element.isNativeMethod() &&
            (element.getClassName().contains("bear.core") || element.getClassName().contains("yishun.log"))) {
          final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
          prefs.edit().putInt(KEY_PROTECT_BEAR, 9).commit();
          return false;
        }
      }
    }
    return true;
  }
}
