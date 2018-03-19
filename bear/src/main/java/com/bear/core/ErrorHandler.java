package com.bear.core;



public interface ErrorHandler {


  void error(String msg);


  void error(String msg, Throwable t);


  void error(String msg, Event event, Throwable t);
}
