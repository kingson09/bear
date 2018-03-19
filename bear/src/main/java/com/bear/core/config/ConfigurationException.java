package com.bear.core.config;



public class ConfigurationException extends RuntimeException {
  private static final long serialVersionUID = -2413951820300775294L;


  public ConfigurationException(final String message) {
    super(message);
  }


  public ConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
