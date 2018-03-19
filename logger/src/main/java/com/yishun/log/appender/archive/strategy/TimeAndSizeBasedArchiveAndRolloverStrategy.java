package com.yishun.log.appender.archive.strategy;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.bear.core.Event;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.config.xml.object.ObjectFactory;
import com.yishun.log.appender.archive.PatternProcessor;
import com.yishun.log.appender.archive.RollingFileManager;
import com.yishun.log.appender.archive.strategy.action.FileDeleteAction;
import com.yishun.log.appender.archive.strategy.action.FileRenameAction;
import com.yishun.log.util.StorageUtils;

import static android.R.id.list;


public final class TimeAndSizeBasedArchiveAndRolloverStrategy implements ArchiveAndRolloverStrategy {
  private final long maxFileSize;
  private final long maxLogSize;
  private long nextArchive;

  private final PatternProcessor.TimePattern interval;
  private final PatternProcessor.TimePattern expire;
  private RollingFileManager manager;

  private TimeAndSizeBasedArchiveAndRolloverStrategy(final String interval, final long fileSize, final String expire,
      final long maxSize) {
    this.interval = PatternProcessor.parsePattern(interval);
    this.maxFileSize = fileSize;
    this.expire = PatternProcessor.parsePattern(expire, false);
    this.maxLogSize = maxSize;
  }


  @Override
  public void initialize(final RollingFileManager manager) {
    this.manager = manager;
    nextArchive = PatternProcessor.getNextTime(manager.getFileTime(), interval);
  }


  @Override
  public boolean isTriggeringEvent(final Event event) {
    if (manager.getFileSize() == 0) {
      return false;
    }
    final long now = System.currentTimeMillis();
    if (now > nextArchive) {
      return true;
    }
    if (manager.getFileSize() > maxFileSize) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "TimeAndSizeBasedArchiveAndRolloverStrategy";
  }


  @ObjectFactory
  public static TimeAndSizeBasedArchiveAndRolloverStrategy createStrategy(
      @ObjectAttribute("interval") final String interval, @ObjectAttribute("size") final String size,
      @ObjectAttribute("expire") final String expire,
      @ObjectAttribute(value = "maxSize", defaultString = "500MB") final String maxSize) {
    final long filesize = PatternProcessor.valueOf(size);
    final long maxLogsize = PatternProcessor.valueOf(maxSize);
    return new TimeAndSizeBasedArchiveAndRolloverStrategy(interval, filesize, expire, maxLogsize);
  }

  @Override
  public FileDeleteAction rollover() {
    File archiveDir = new File(manager.getArchiveDir());
    final long expireTime = PatternProcessor.getNextTime(System.currentTimeMillis(), this.expire);

    if (archiveDir.exists()) {
      ArrayList<File> deleteLogFiles = new ArrayList<File>();
      File[] fs = archiveDir.listFiles();
      if (fs == null || fs.length == 0) {
        return null;
      }
      Arrays.sort(fs, new Comparator<File>() {
        public int compare(File f1, File f2) {
          long diff = f1.lastModified() - f2.lastModified();
          if (diff > 0) {
            return 1;
          } else if (diff == 0) {
            return 0;
          } else {
            return -1;
          }
        }

        public boolean equals(Object obj) {
          return true;
        }

      });
      long logsSize = 0;
      for (File f : fs) {
        logsSize += f.length();
      }
      long deleteLogSize = 0;
      if (logsSize > maxLogSize) {
        deleteLogSize = logsSize - (long) (maxLogSize * 0.8);
      }
      long freesize = StorageUtils.getSDcardFreeSpace();
      if (freesize > 0 && logsSize > freesize) {
        long deleteFreeSize = logsSize - (long) (freesize * 0.5);
        deleteLogSize = deleteFreeSize > deleteLogSize ? deleteFreeSize : deleteLogSize;
      }
      long size = 0;
      for (File file : fs) {
        if (size < deleteLogSize || file.lastModified() < expireTime) {
          size += file.length();
          deleteLogFiles.add(file);
        }
      }

      if (!deleteLogFiles.isEmpty()) {
        return new FileDeleteAction(deleteLogFiles);
      }
    }
    return null;
  }

  @Override
  public FileRenameAction archive() throws SecurityException {
    nextArchive = PatternProcessor.getNextTime(System.currentTimeMillis(), interval);
    final File currentFile = new File(manager.getFileName());
    final String renameTo = manager.getArchiveDir() + PatternProcessor.format(currentFile.lastModified()) + ".log";
    return new FileRenameAction(currentFile, new File(renameTo), false);

  }
}