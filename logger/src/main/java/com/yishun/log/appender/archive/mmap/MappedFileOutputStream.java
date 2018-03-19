package com.yishun.log.appender.archive.mmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by bjliuzhanyong on 2018/2/28.
 */

public class MappedFileOutputStream extends FileOutputStream {

  private long buffersize;
  private long filesize;
  protected FileChannel channel;
  protected MappedByteBuffer buffer;


  public MappedFileOutputStream(File file, final long buffersize) throws FileNotFoundException,
      MappedBufferCreateFailedException {
    super(file);
    this.buffersize = buffersize;
    try {
      channel = new RandomAccessFile(file, "rw").getChannel();
      findPos(file);
      buffer = channel.map(FileChannel.MapMode.READ_WRITE, filesize, buffersize);
    } catch (FileNotFoundException e) {
      throw new MappedBufferCreateFailedException();
    } catch (IOException e) {
      throw new MappedBufferCreateFailedException();
    }
  }

  private void findPos(File file) {
    try {
      long fsize = file.length();
      long start = fsize % buffersize;
      MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start, fsize);
      int a = -1, b = -1;
      while (a == 0 && b == 0 && buffer.hasRemaining()) {
        a = buffer.get();
        b = buffer.get();
      }
      if (buffer.hasRemaining()) {
        this.filesize = fsize - buffer.remaining();
      } else {
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fsize);
        a = -1;
        b = -1;
        while (a == 0 && b == 0 && buffer.hasRemaining()) {
          a = buffer.get();
          b = buffer.get();
        }
        if (buffer.hasRemaining()) {
          this.filesize = fsize - buffer.remaining();
        } else {
          this.filesize = fsize;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  public void flush() throws IOException {

    buffer.force();
    filesize += buffersize;
    buffer = channel.map(FileChannel.MapMode.READ_WRITE, filesize, buffersize);
  }

  public void close() throws IOException {
    buffer.force();
    filesize += buffersize;
    channel.close();
  }

  public void write(byte[] bytes) throws IOException {
    write(bytes, 0, bytes.length);
  }

  public void write(byte[] bytes, int offset, int count) throws IOException {
    if (!buffer.hasRemaining()) {
      flush();
    }
    if (buffer.remaining() < count) {
      int remaining = buffer.remaining();
      buffer.put(bytes, offset, remaining);
      write(bytes, offset + remaining, count - remaining);
    } else {
      buffer.put(bytes, offset, count);
    }
  }

  @Override
  public void write(int i) throws IOException {

  }

}
