package com.test;

import android.app.Application;

import com.bear.core.util.StatusLogger;
import com.yishun.log.AndroidLog;


/**
 * Created by bjliuzhanyong on 2017/8/9.
 */

public class app extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    StatusLogger.setLevel(StatusLogger.LEVEL_VERBOSE);
    AndroidLog.init(this, new AndroidLookup(this));
  }
}
