package com.yishun.log.appender.archive.mmap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bjliuzhanyong on 2018/3/13.
 */

public class MappedBufferedOutputStream extends OutputStream {

  protected OutputStream out;
  protected PersistenceMappedByteBuffer buffer;


  public MappedBufferedOutputStream(final String bufferFolder, final String bufferName, final int bufferSize,
      final OutputStream file) throws MappedBufferCreateFailedException {
    out = file;
    buffer = new PersistenceMappedByteBuffer(bufferFolder, bufferName, bufferSize);
  }

  public long getBufferSize() {
    return buffer.size();
  }

  public void flush() throws IOException {
    buffer.flip();
    byte[] tmp = new byte[buffer.size()];
    buffer.getRealBuffer().get(tmp);
    out.write(tmp);
    buffer.clear();
    out.flush();
  }

  public void close() throws IOException {
    flush();
    buffer.close();
    out.close();
  }

  public void write(byte[] bytes) throws IOException {
    write(bytes, 0, bytes.length);
  }

  public void write(byte[] bytes, int offset, int count) throws IOException {
    if (count >= buffer.capacity()) {
      flush();
      buffer.put(bytes, offset, buffer.capacity());
      write(bytes, offset + buffer.capacity(), count - buffer.capacity());
      return;
    }
    if (count > buffer.remaining()) {
      flush();
    }
    buffer.put(bytes, offset, count);
  }


  @Override
  public void write(int i) throws IOException {

  }


}
