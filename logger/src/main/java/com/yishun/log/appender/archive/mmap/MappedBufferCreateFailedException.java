package com.yishun.log.appender.archive.mmap;

/**
 * Created by bjliuzhanyong on 2018/2/28.
 */

public class MappedBufferCreateFailedException extends Exception {
  public MappedBufferCreateFailedException() {

  }

  public MappedBufferCreateFailedException(String detailMessage) {
    super(detailMessage);
  }
}
