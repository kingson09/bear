package com.yishun.log.util.snappy;

import java.io.IOException;
import java.io.OutputStream;

import com.jiechic.library.android.snappy.Snappy;
import com.yishun.log.appender.archive.mmap.MappedBufferCreateFailedException;
import com.yishun.log.appender.archive.mmap.PersistenceMappedByteBuffer;

/**
 * Created by bjliuzhanyong on 2018/3/13.
 */

public class MappedSnappyOutputStream extends OutputStream {
  static final int MIN_BLOCK_SIZE = 1 * 1024;
  static final int DEFAULT_BLOCK_SIZE = 32 * 1024; // Use 32kb for the default block size

  protected final OutputStream out;
  private final int blockSize;


  protected PersistenceMappedByteBuffer inputBuffer;
  private boolean headerWritten;
  private boolean closed;

  public MappedSnappyOutputStream(OutputStream out, final String bufferFolder, final String bufferName,
      int blockSize) throws MappedBufferCreateFailedException {
    this.out = out;
    this.blockSize = Math.max(MIN_BLOCK_SIZE, blockSize);
    int inputSize = blockSize;
    inputBuffer = new PersistenceMappedByteBuffer(bufferFolder, bufferName, inputSize);
  }

  @Override
  public void write(byte[] b, int byteOffset, int byteLength) throws IOException {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    if (byteLength >= inputBuffer.capacity()) {
      compressInput();
      inputBuffer.put(b, byteOffset, inputBuffer.capacity());
      write(b, byteOffset + inputBuffer.capacity(), byteLength - inputBuffer.capacity());
      return;
    }
    if (byteLength > inputBuffer.remaining()) {
      compressInput();
    }
    inputBuffer.put(b, byteOffset, byteLength);
  }


  @Override
  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    if (!inputBuffer.hasRemaining()) {
      compressInput();
    }
    inputBuffer.put((byte) b);
  }

  static void writeInt(OutputStream out, int v) throws IOException {
    byte[] b = new byte[4];
    b[3] = (byte) (v & 0xff);
    b[2] = (byte) (v >> 8 & 0xff);
    b[1] = (byte) (v >> 16 & 0xff);
    b[0] = (byte) (v >> 24 & 0xff);
    out.write(b);
  }

  static int readInt(byte[] buffer, int pos) {
    int b1 = (buffer[pos] & 0xFF) << 24;
    int b2 = (buffer[pos + 1] & 0xFF) << 16;
    int b3 = (buffer[pos + 2] & 0xFF) << 8;
    int b4 = buffer[pos + 3] & 0xFF;
    return b1 | b2 | b3 | b4;
  }

  protected void compressInput() throws IOException {
    if (inputBuffer.size() == 0) {
      return;
    }
    inputBuffer.flip();
    byte[] tmp = new byte[inputBuffer.remaining()];
    inputBuffer.get(tmp);
    byte[] compressed = Snappy.compress(tmp);
    // Write compressed data size
    writeInt(out, compressed.length);
    out.write(compressed);
    inputBuffer.clear();
  }

  @Override
  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    compressInput();
    out.flush();
  }


  @Override
  public void close() throws IOException {
    if (closed) {
      return;
    }
    try {
      flush();
      out.close();
    } finally {
      closed = true;
      inputBuffer = null;
    }
  }
}

