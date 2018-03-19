package com.yishun.log.format;

import android.text.TextUtils;

import com.bear.core.config.Configuration;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectConfiguration;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.format.Header;

/**
 * Created by bjliuzhanyong on 2017/9/11.
 */

public class LogfileHeader implements Header<byte[]> {
  private static final String lineSep = "\0";
  private final String name;
  private final String header;
  private final Configuration config;

  public LogfileHeader(final String name, final String header, final Configuration config) {
    this.name = name;
    this.header = header;
    this.config = config;
  }

  @Override
  public byte[] getHeader() {
    return strSubstitutorReplace(header + lineSep).getBytes();
  }

  private String strSubstitutorReplace(final String str) {
    if (!TextUtils.isEmpty(str) && config != null) {
      return config.getStrSubstitutor().replace(str);
    }
    return str;
  }

  @ObjectFactory
  public static LogfileHeader createHeader(@ObjectAttribute("name") final String name,
      @ObjectAttribute("header") final String header, @ObjectConfiguration final Configuration config) {

    return new LogfileHeader(name, header, config);
  }
}
