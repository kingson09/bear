package com.yishun.log.appender.archive.strategy.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class FileRenameAction extends AbstractAction {


  private final File source;


  private final File destination;


  private final boolean renameEmptyFiles;


  public FileRenameAction(final File src, final File dst, final boolean renameEmptyFiles) {
    source = src;
    destination = dst;
    this.renameEmptyFiles = renameEmptyFiles;
  }


  @Override
  public boolean execute() {
    return execute(source, destination, renameEmptyFiles);
  }


  public static boolean execute(final File source, final File destination, final boolean renameEmptyFiles) {
    if (renameEmptyFiles || source.length() > 0) {
      final File parent = destination.getParentFile();
      if (parent != null && !parent.exists()) {
        // LOG4J2-679: ignore mkdirs() result: in multithreaded scenarios,
        // if one thread succeeds the other thread returns false
        // even though directories have been created. Check if dir exists instead.
        parent.mkdirs();
        if (!parent.exists()) {
          LOGGER.error("Unable to create directory {}", parent.getAbsolutePath());
          return false;
        }
      }
      try {
        if (!source.renameTo(destination)) {
          try {
            copyFile(source, destination);
            return source.delete();
          } catch (final IOException iex) {
            LOGGER.error("Unable to rename file {} to {} - {}", source.getAbsolutePath(), destination.getAbsolutePath(),
                iex.getMessage());
          }
        }
        return true;
      } catch (final Exception ex) {
        try {
          copyFile(source, destination);
          return source.delete();
        } catch (final IOException iex) {
          LOGGER.error("Unable to rename file {} to {} - {}", source.getAbsolutePath(), destination.getAbsolutePath(),
              iex.getMessage());
        }
      }
    } else {
      try {
        source.delete();
        return true;
      } catch (final Exception ex) {
        LOGGER.error("Unable to delete empty file " + source.getAbsolutePath());
      }
    }

    return false;
  }

  private static void copyFile(final File source, final File destination) throws IOException {
    if (!destination.exists()) {
      destination.createNewFile();
    }

    FileChannel srcChannel = null;
    FileChannel destChannel = null;
    FileInputStream srcStream = null;
    FileOutputStream destStream = null;
    try {
      srcStream = new FileInputStream(source);
      destStream = new FileOutputStream(destination);
      srcChannel = srcStream.getChannel();
      destChannel = destStream.getChannel();
      destChannel.transferFrom(srcChannel, 0, srcChannel.size());
    } finally {
      if (srcChannel != null) {
        srcChannel.close();
      }
      if (srcStream != null) {
        srcStream.close();
      }
      if (destChannel != null) {
        destChannel.close();
      }
      if (destStream != null) {
        destStream.close();
      }
    }
  }

  @Override
  public String toString() {
    return FileRenameAction.class.getSimpleName() + '[' + source + " to " + destination //
        + ", renameEmptyFiles=" + renameEmptyFiles + ']';
  }
}
