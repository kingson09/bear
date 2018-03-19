package com.yishun.log.appender.archive.mmap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by L460 on 2018/3/11.
 */

public class PersistenceMappedByteBuffer {
  private RandomAccessFile bufferfileAccess;
  private FileChannel channel;
  private MappedByteBuffer buffer;

  public PersistenceMappedByteBuffer(final String bufferFolder, final String bufferName, final int bufferSize) throws
      MappedBufferCreateFailedException {
    File folder = null;
    File bufferFile = null;
    try {
      folder = new File(bufferFolder);
      if (!folder.exists()) {
        folder.mkdirs();
      }
      bufferFile = new File(folder.getAbsolutePath() + File.separator + bufferName);
      bufferfileAccess = new RandomAccessFile(bufferFile, "rw");
      channel = bufferfileAccess.getChannel();
      buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize + 4);
      int position = this.buffer.getInt(0);
      position(position == 0 ? 4 : position);
    } catch (Exception e) {
      try {
        if (bufferFile != null) {
          bufferFile.deleteOnExit();
        }
        folder = new File(bufferFolder);
        if (!folder.exists()) {
          folder.mkdirs();
        }
        bufferFile = new File(folder.getAbsolutePath() + File.separator + bufferName);
        bufferfileAccess = new RandomAccessFile(bufferFile, "rw");
        channel = bufferfileAccess.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize + 4);
        int position = this.buffer.getInt(0);
        position(position == 0 ? 4 : position);
      } catch (Exception e1) {
        throw new MappedBufferCreateFailedException(e1.toString() + " " + e1.getMessage());
      }
    }

  }

  public PersistenceMappedByteBuffer put(byte b) {
    buffer.put(b);
    buffer.putInt(0, buffer.position());
    return this;
  }


  public PersistenceMappedByteBuffer put(byte[] src) {
    return put(src, 0, src.length);
  }

  public PersistenceMappedByteBuffer put(byte[] src, int srcOffset, int byteCount) {
    buffer.put(src, srcOffset, byteCount);
    buffer.putInt(0, buffer.position());
    return this;
  }

  public int capacity() {
    return buffer.capacity() - 4;
  }

  public PersistenceMappedByteBuffer clear() {
    buffer.clear();
    position(4);
    return this;
  }

  public PersistenceMappedByteBuffer flip() {
    buffer.flip();
    if (buffer.limit() < 4) {
      buffer.limit(4);
    }
    position(4);
    return this;
  }

  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }

  public int remaining() {
    return buffer.remaining();
  }

  public ByteBuffer getRealBuffer() {
    return buffer;
  }

  public int size() {
    int position = buffer.position();
    return position > 4 ? position - 4 : 0;
  }

  public void close() throws IOException {
    channel.close();
    bufferfileAccess.close();
  }

  public PersistenceMappedByteBuffer position(int newPosition) {
    buffer.position(newPosition);
    buffer.putInt(0, newPosition);
    return this;
  }

  public PersistenceMappedByteBuffer get(byte[] dst) {
    buffer.get(dst);
    return this;
  }
}
