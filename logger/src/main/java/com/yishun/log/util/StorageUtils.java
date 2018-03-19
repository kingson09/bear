package com.yishun.log.util;

import java.io.File;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

/**
 * Created by L460 on 2018/3/4.
 */

public class StorageUtils {
  public static long getSDcardFreeSpace() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      File sdcardDir = Environment.getExternalStorageDirectory();
      StatFs sf = new StatFs(sdcardDir.getPath());
      if (sf != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          return sf.getAvailableBytes();
        } else {
          return sf.getAvailableBlocksLong() * sf.getBlockSizeLong();
        }
      }
    }
    return -1l;
  }
}
