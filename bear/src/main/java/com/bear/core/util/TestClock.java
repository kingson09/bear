package com.bear.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L460 on 2018/3/2.
 */

public class TestClock {
  private static StatusLogger logger = StatusLogger.getLogger();
  private boolean open = false;
  private static HashMap<String, TestClock> clocks = new HashMap<>();

  public synchronized static TestClock getClock(String clockName) {
    TestClock clock = clocks.get(clockName);
    if (clock == null) {
      clock = new TestClock();
      clocks.put(clockName, clock);
    }
    return clock;
  }

  private TestClock() {

  }

  private long time;
  private long start;

  public void start() {
    if (open) {
      start = System.currentTimeMillis();
    }
  }

  public long stop() {
    if (open) {
      time += System.currentTimeMillis() - start;
    }
    return time;
  }

  public long getTime() {
    return time;
  }

  public void reset() {
    time = 0;
  }

  public static void resetAll() {
    for (TestClock cl : clocks.values()) {
      cl.reset();
    }
  }

  public static void printTimeCunsuming() {
    for (Map.Entry<String, TestClock> cl : clocks.entrySet()) {
      logger.debug("{0} consuming:{1}", cl.getKey(), cl.getValue().getTime());
    }
  }

}
