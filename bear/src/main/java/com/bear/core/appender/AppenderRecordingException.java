package com.bear.core.appender;

import com.bear.core.RecordingException;



public class AppenderRecordingException extends RecordingException {

  private static final long serialVersionUID = 6545990597472958303L;


  public AppenderRecordingException(final String message) {
    super(message);
  }


  public AppenderRecordingException(final String message, final Throwable cause) {
    super(message, cause);
  }


  public AppenderRecordingException(final Throwable cause) {
    super(cause);
  }
}
