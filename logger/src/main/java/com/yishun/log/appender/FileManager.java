package com.yishun.log.appender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class FileManager extends OutputStreamManager {

  private static final FileManagerFactory FACTORY = new FileManagerFactory();

  private final boolean isAppend;
  private final boolean isLocking;
  private final int bufferSize;
  private final boolean compress;
  protected boolean isNewFile;

  protected FileManager(final String fileName, final OutputStream os, final boolean append, final boolean locking,
      final int bufferSize, final boolean compress, final boolean isNewFile) {
    super(os, fileName);
    this.isAppend = append;
    this.isLocking = locking;
    this.bufferSize = bufferSize;
    this.compress = compress;
    this.isNewFile = isNewFile;
  }


  public static FileManager getFileManager(final String fileName, final boolean append, boolean locking,
      final boolean bufferedIo, final int bufferSize, final boolean compress) {

    if (locking && bufferedIo) {
      locking = false;
    }
    return (FileManager) getManager(fileName, new FactoryData(append, locking, bufferedIo, bufferSize, compress),
        FACTORY);
  }

  @Override
  protected synchronized void write(final byte[] bytes, final int offset, final int length) {

    if (isLocking) {
      final FileChannel channel = ((FileOutputStream) getOutputStream()).getChannel();
      try {

        final FileLock lock = channel.lock(0, Long.MAX_VALUE, false);
        try {
          super.write(bytes, offset, length);
        } finally {
          lock.release();
        }
      } catch (final IOException ex) {
        throw new AppenderLoggingException("Unable to obtain lock on " + getName(), ex);
      }

    } else {
      super.write(bytes, offset, length);
    }
  }


  public String getFileName() {
    return getName();
  }


  public boolean isAppend() {
    return isAppend;
  }


  public boolean isLocking() {
    return isLocking;
  }


  public int getBufferSize() {
    return bufferSize;
  }

  public boolean isCompress() {
    return this.compress;
  }

  public boolean isNewFile() {
    return this.isNewFile;
  }

  public void writeHeader(byte[] header) {
    this.isNewFile = false;
    super.write(header);
  }

  public void writeFooter(byte[] footer) {
    super.write(footer);
  }

  private static class FactoryData {
    private final boolean append;
    private final boolean locking;
    private final boolean bufferedIO;
    private final int bufferSize;
    private final boolean compress;


    public FactoryData(final boolean append, final boolean locking, final boolean bufferedIO, final int bufferSize,
        final boolean compress) {
      this.append = append;
      this.locking = locking;
      this.bufferedIO = bufferedIO;
      this.bufferSize = bufferSize;
      this.compress = compress;
    }
  }


  private static class FileManagerFactory implements ManagerFactory<FileManager, FactoryData> {


    @Override
    public FileManager createManager(final String name, final FactoryData data) {
      final File file = new File(name);
      final File parent = file.getParentFile();
      if (null != parent && !parent.exists()) {
        parent.mkdirs();
      }
      try {
        file.createNewFile();
      } catch (final IOException ioe) {
        LOGGER.error("Unable to create file " + name, ioe);
        return null;
      }
      boolean isNewFile = data.append ? file.length() == 0 : true;
      OutputStream os;
      try {
        os = new FileOutputStream(name, data.append);
        int bufferSize = data.bufferSize;
        if (data.bufferedIO) {
          os = new BufferedOutputStream(os, bufferSize);
        } else {
          bufferSize = -1; // signals to RollingFileManager not to use BufferedOutputStream
        }
        return new FileManager(name, os, data.append, data.locking, bufferSize, data.compress,
            !data.append || isNewFile);
      } catch (final FileNotFoundException ex) {
        LOGGER.error("FileManager (" + name + ") " + ex);
      }
      return null;
    }
  }
}
