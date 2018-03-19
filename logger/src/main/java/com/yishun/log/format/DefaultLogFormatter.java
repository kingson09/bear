package com.yishun.log.format;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.text.TextUtils;

import com.bear.core.Formatter;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectFactory;
import com.yishun.log.event.LogEvent;
import com.yishun.log.util.TestClock;


public class DefaultLogFormatter implements Formatter<LogEvent, byte[]> {
  private static final char lineSep = '\0';
  private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";
  private static final String TAG_LOCATION = "\"location\":";
  private static final String TAG_LOG_TIME = "\"logWriteTime\":";
  private static final String TAG_MESSAGE = "\"log\":";
  private static final String TAG_LOG_TAG = "\"logFunction\":";
  private static final String TAG_LEVEL = "\"level\":";
  private final String name;
  private Calendar calendar = GregorianCalendar.getInstance();
  private Charset charset = Charset.forName("UTF-8");
  private TestClock clock = TestClock.getClock("formatter");

  public DefaultLogFormatter(final String name) {
    this.name = name;

  }

  public String formatTimeCalendar(long time) {
    StringBuilder timesb = new StringBuilder(23);
    calendar.setTimeInMillis(time);
    timesb.append(calendar.get(Calendar.YEAR));
    timesb.append('-');
    int d = 1 + calendar.get(Calendar.MONTH);
    if (d < 10) {
      timesb.append('0');
    }
    timesb.append(1 + calendar.get(Calendar.MONTH));
    timesb.append('-');
    d = calendar.get(Calendar.DAY_OF_MONTH);
    if (d < 10) {
      timesb.append('0');
    }
    timesb.append(calendar.get(Calendar.DAY_OF_MONTH));
    timesb.append(' ');
    d = calendar.get(Calendar.HOUR_OF_DAY);
    if (d < 10) {
      timesb.append('0');
    }
    timesb.append(calendar.get(Calendar.HOUR_OF_DAY));
    timesb.append(':');
    d = calendar.get(Calendar.MINUTE);
    if (d < 10) {
      timesb.append('0');
    }
    timesb.append(calendar.get(Calendar.MINUTE));
    timesb.append(':');
    d = calendar.get(Calendar.SECOND);
    if (d < 10) {
      timesb.append('0');
    }
    timesb.append(calendar.get(Calendar.SECOND));
    timesb.append(':');
    d = calendar.get(Calendar.MILLISECOND);
    if (d < 100) {
      timesb.append('0');
      if (d < 10) {
        timesb.append('0');
      }
    }
    timesb.append(calendar.get(Calendar.MILLISECOND));
    return timesb.toString();
  }

  @Override
  public byte[] format(LogEvent event) {
    clock.start();
    String location = null, log_time = null;
    if (event.isIncludeLocation()) {
      final StackTraceElement element = event.getSource();
      if (element != null) {
        location = element.toString();
      }
    }
    if (event.getTimeMillis() != 0) {
      log_time = formatTimeCalendar(event.getTimeMillis());
    }
    StringBuilder sb = new StringBuilder(100);
    sb.append('{');
    if (!TextUtils.isEmpty(location)) {
      sb.append(TAG_LOCATION);
      quote(sb, location);
      sb.append(',');
    }
    if (!TextUtils.isEmpty(log_time)) {
      sb.append(TAG_LOG_TIME);
      quote(sb, log_time);
      sb.append(',');
    }
    if (!TextUtils.isEmpty(event.getMessage())) {
      sb.append(TAG_MESSAGE);
      quote(sb, "[" + event.getThreadName() + "] " + event.getMessage());
      sb.append(',');
    }
    if (!TextUtils.isEmpty(event.getTag())) {
      sb.append(TAG_LOG_TAG);
      quote(sb, event.getTag());
      sb.append(',');
    }
    sb.append(TAG_LEVEL);
    quote(sb, event.getLevel().toString());
    sb.append('}');
    sb.append(lineSep);
    byte[] result = sb.toString().getBytes(charset);
    clock.start();
    return result;
  }

  public void quote(StringBuilder sb, String string) {
    if (string == null) {
      return;
    }
    char b;
    char c = 0;
    String hhhh;
    int i;
    int len = string.length();

    sb.append('"');
    for (i = 0; i < len; i += 1) {
      b = c;
      c = string.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '/':
          if (b == '<') {
            sb.append('\\');
          }
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
            sb.append("\\u");
            hhhh = Integer.toHexString(c);
            sb.append("0000", 0, 4 - hhhh.length());
            sb.append(hhhh);
          } else {
            sb.append(c);
          }
      }
    }
    sb.append('"');
  }

  @ObjectFactory
  public static com.yishun.log.format.DefaultLogFormatter createFormatter(@ObjectAttribute("name") final String name) {

    return new com.yishun.log.format.DefaultLogFormatter(name);
  }

}

