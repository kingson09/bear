package com.test;

import java.io.File;

import android.content.Context;

import com.bear.core.Event;
import com.bear.core.lookup.StrLookup;
import com.yishun.log.AndroidLog;

/**
 * Created by bjliuzhanyong on 2017/11/2.
 */

public class AndroidLookup implements StrLookup {
  private Context appContext;

  public AndroidLookup(Context context) {
    this.appContext = context;
  }

  public String lookup(String key) {

    if (key.equals("appdir")) {
      File f = appContext.getExternalFilesDir(null);
      if (f != null) {
        return new File(f, "logs").getAbsolutePath();
      }
      return new File(appContext.getFilesDir(), "logs").getAbsolutePath();
    } else if (key.equals("device")) {
      return "p8";
    } else if (key.equals("channel")) {
      return "360";
    } else if (key.equals("uid")) {
      return "79979798";
    } else if (key.equals("product")) {
      return "rrz";
    } else if (key.equals("platform")) {
      return "android";
    } else if (key.equals("logLevel")) {
      return AndroidLog.getLogLevel().toString();
    } else if (key.equals("appCode")) {
      return "test";
    }

    return null;
  }


  public String lookup(Event event, String key) {
    return lookup(key);
  }
}
