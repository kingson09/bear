package com.yishun.log.appender;



public class AppenderLoggingException extends RuntimeException {

  public AppenderLoggingException(final String message) {
    super(message);
  }


  public AppenderLoggingException(final String message, final Throwable cause) {
    super(message, cause);
  }


  public AppenderLoggingException(final Throwable cause) {
    super(cause);
  }
}
