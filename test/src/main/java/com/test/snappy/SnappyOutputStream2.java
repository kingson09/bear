package com.test.snappy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.jiechic.library.android.snappy.Snappy;
import com.yishun.log.appender.archive.mmap.MappedBufferCreateFailedException;

/**
 * Created by bjliuzhanyong on 2018/3/13.
 */

public class SnappyOutputStream2 extends OutputStream {
  static final int MIN_BLOCK_SIZE = 1 * 1024;
  static final int DEFAULT_BLOCK_SIZE = 32 * 1024; // Use 32kb for the default block size

  protected final OutputStream out;
  private final int blockSize;


  // The input and output buffer fields are set to null when closing this stream:
  protected ByteBuffer inputBuffer;
  protected byte[] outputBuffer;
  private int outputCursor = 0;
  private boolean headerWritten;
  private boolean closed;

  public SnappyOutputStream2(OutputStream out, String bufferFolder, String bufferName, int blockSize) throws
      MappedBufferCreateFailedException {
    this.out = out;
    this.blockSize = Math.max(MIN_BLOCK_SIZE, blockSize);
    int inputSize = blockSize;
    int outputSize = 256 * 1024;

    inputBuffer = ByteBuffer.allocate(inputSize);
    outputBuffer = new byte[outputSize];
  }


  /* (non-Javadoc)
   * @see java.io.OutputStream#write(byte[], int, int)
   */
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

  /* (non-Javadoc)
   * @see java.io.OutputStream#flush()
   */
  @Override
  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Stream is closed");
    }
    compressInput();
    dumpOutput();
  }

  static void writeInt(ByteBuffer dst, int offset, int v) {
    dst.putInt(offset, v);
  }

  static void writeInt(byte[] dst, int offset, int v) {
    dst[offset] = (byte) ((v >> 24) & 0xFF);
    dst[offset + 1] = (byte) ((v >> 16) & 0xFF);
    dst[offset + 2] = (byte) ((v >> 8) & 0xFF);
    dst[offset + 3] = (byte) ((v >> 0) & 0xFF);
  }

  static int readInt(ByteBuffer buffer, int pos) {
    return buffer.getInt();
  }

  protected void dumpOutput() throws IOException {
    if (outputCursor > 0) {
      out.write(outputBuffer, 0, outputCursor);
      outputCursor = 0;
    }
  }

  private boolean hasSufficientOutputBufferFor(int inputSize) {
    int maxCompressedSize = Snappy.maxCompressedLength(inputSize);
    return maxCompressedSize < outputBuffer.length - outputCursor - 4;
  }

  protected void compressInput() throws IOException {
    if (inputBuffer.position() == 0) {
      return; // no need to dump
    }

    if (!headerWritten) {
      outputCursor = writeHeader();
      headerWritten = true;
    }

    if (!hasSufficientOutputBufferFor(inputBuffer.position())) {
      dumpOutput();
    }
    int compressedSize =
        Snappy.compress(inputBuffer.array(), 0, inputBuffer.position(), outputBuffer, outputCursor + 4);
    // Write compressed data size
    writeInt(outputBuffer, outputCursor, compressedSize);
    outputCursor += 4 + compressedSize;
    inputBuffer.clear();
  }

  protected int writeHeader() {
    return SnappyCodec.currentHeader.writeHeader(outputBuffer, 0);
  }


  /**
   * close the stream
   */
    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
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
      outputBuffer = null;
    }
  }
}

