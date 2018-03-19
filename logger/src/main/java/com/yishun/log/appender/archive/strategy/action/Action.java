package com.yishun.log.appender.archive.strategy.action;

public interface Action extends Runnable {

  boolean execute();


  void close();


  boolean isComplete();
}