package com.yishun.log.appender.archive;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yishun.log.util.StatusLogger;


public class PatternProcessor {
  protected static final StatusLogger LOGGER = StatusLogger.getLogger();

  private static final String MONTH_CHAR = "M";
  private static final String[] DAY_CHARS = { "D", "d" };
  private static final String[] HOUR_CHARS = { "H", "h" };
  private static final String MINUTE_CHAR = "m";
  private static final String SECOND_CHAR = "s";
  private static final String MILLIS_CHAR = "S";
  private static final String pattern =
      "^([1-9]\\d*)(['M''D''d''H''d''m''s''S'])$";
  private static final Pattern regex = Pattern.compile(pattern);

  private static final long KB = 1024;
  private static final long MB = KB * KB;
  private static final long GB = KB * MB;
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // the default max size 10 MB
  private static final Pattern VALUE_PATTERN =
      Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G)B?", Pattern.CASE_INSENSITIVE);


  private static int calculateUnit(final String unitStr) {
    if (MILLIS_CHAR.equals(unitStr)) {
      return Calendar.MILLISECOND;
    } else if (SECOND_CHAR.equals(unitStr)) {
      return Calendar.SECOND;
    } else if (MINUTE_CHAR.equals(unitStr)) {
      return Calendar.MINUTE;
    } else if (HOUR_CHARS.equals(unitStr)) {
      return Calendar.HOUR_OF_DAY;
    } else if (DAY_CHARS.equals(unitStr)) {
      return Calendar.DATE;
    } else if (MONTH_CHAR.equals(unitStr)) {
      return Calendar.MONTH;
    }

    return Calendar.DATE;
  }


  public static String format(final long time) {
    return new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(new Date(time));
  }

  public static long valueOf(final String string) {
    if (string == null) {
      return MAX_FILE_SIZE;
    }
    final Matcher matcher = VALUE_PATTERN.matcher(string);

    // Valid input?
    if (matcher.matches()) {
      try {
        // Get double precision value
        final long value = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher.group(1)).longValue();

        // Get units specified
        final String units = matcher.group(3);

        if (units.isEmpty()) {
          return value;
        } else if (units.equalsIgnoreCase("K")) {
          return value * KB;
        } else if (units.equalsIgnoreCase("M")) {
          return value * MB;
        } else if (units.equalsIgnoreCase("G")) {
          return value * GB;
        } else {
          LOGGER.error("Units not recognized: " + string);
          return MAX_FILE_SIZE;
        }
      } catch (final ParseException e) {
        LOGGER.error("Unable to parse numeric part: " + string, e);
        return MAX_FILE_SIZE;
      }
    }
    LOGGER.error("Unable to parse bytes: " + string);
    return MAX_FILE_SIZE;
  }


  public static long getNextTime(final long time, final TimePattern shift) {
    final Calendar currentCal = Calendar.getInstance();
    currentCal.setTimeInMillis(time);
    final Calendar cal = Calendar.getInstance();
    cal.set(currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), 1, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    if (shift.getUnit() == Calendar.MONTH) {
      cal.add(Calendar.MONTH, shift.getInterval());
      return cal.getTimeInMillis();
    }
    cal.set(Calendar.DAY_OF_YEAR, currentCal.get(Calendar.DAY_OF_YEAR));
    if (shift.getUnit() == Calendar.DATE) {
      cal.add(Calendar.DATE, shift.getInterval());
      return cal.getTimeInMillis();
    }
    cal.set(Calendar.HOUR_OF_DAY, currentCal.get(Calendar.HOUR_OF_DAY));
    if (shift.getUnit() == Calendar.HOUR_OF_DAY) {
      cal.add(Calendar.HOUR_OF_DAY, shift.getInterval());
      return cal.getTimeInMillis();
    }
    cal.set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE));
    if (shift.getUnit() == Calendar.MINUTE) {
      cal.add(Calendar.MINUTE, shift.getInterval());
      return cal.getTimeInMillis();
    }
    cal.set(Calendar.SECOND, currentCal.get(Calendar.SECOND));
    if (shift.getUnit() == Calendar.SECOND) {
      cal.add(Calendar.SECOND, shift.getInterval());
      return cal.getTimeInMillis();
    }
    cal.set(Calendar.MILLISECOND, currentCal.get(Calendar.MILLISECOND));
    cal.add(Calendar.MILLISECOND, shift.getInterval());
    return cal.getTimeInMillis();
  }

  public static TimePattern parsePattern(String pattern, boolean positive) {
    Matcher m = regex.matcher(pattern);
    if (m.find()) {
      return new TimePattern(positive ? Integer.parseInt(m.group(1)) : Integer.parseInt(m.group(1)) * -1,
          calculateUnit(m.group(2)));
    }
    return null;
  }

  public static TimePattern parsePattern(String pattern) {
    return parsePattern(pattern, true);
  }

  public static class TimePattern {
    private final int interval;
    private final int unit;

    public TimePattern(final int interval, final int unit) {
      this.interval = interval;
      this.unit = unit;
    }

    public int getInterval() {
      return interval;
    }

    public int getUnit() {
      return unit;
    }
  }

}
