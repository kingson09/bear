package com.yishun.log.appender.archive.mmap;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bjliuzhanyong on 2018/2/28.
 */

public class MappedBufferedFileOutputStream extends MappedBufferedOutputStream {


  public MappedBufferedFileOutputStream(final String bufferFolder, final String bufferName, final int bufferSize,
      final FileOutputStream file) throws MappedBufferCreateFailedException {
    super(bufferFolder, bufferName, bufferSize, file);
  }

  @Override
  public void flush() throws IOException {
    buffer.flip();
    ((FileOutputStream) out).getChannel().write(buffer.getRealBuffer());
    buffer.clear();
    out.flush();
  }


}
