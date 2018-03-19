package com.yishun.log;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.bear.core.config.Configuration;
import com.bear.core.config.xml.ConfigurationSource;
import com.bear.core.config.xml.XmlConfiguration;
import com.logger.R;
import com.yishun.log.lookup.AndroidLookup;

/**
 * Created by bjliuzhanyong on 2017/10/12.
 */

public class DefaultXMLConfigureFactory {
  public static XmlConfiguration getConfiguration(Context context) {
    InputStream stream = context.getResources().openRawResource(R.raw.log_debug);
    ConfigurationSource source = null;
    try {
      source = new ConfigurationSource(stream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    XmlConfiguration config = new XmlConfiguration(source);
    return config;
  }
}
