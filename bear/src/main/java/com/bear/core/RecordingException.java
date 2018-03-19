package com.bear.core;



public class RecordingException extends RuntimeException {

  private static final long serialVersionUID = 6366395965071580537L;


  public RecordingException(final String message) {
    super(message);
  }


  public RecordingException(final String message, final Throwable cause) {
    super(message, cause);
  }


  public RecordingException(final Throwable cause) {
    super(cause);
  }
}
