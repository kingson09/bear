package com.bear.core.command;


public interface CommandExecuteHandler<T> {
  void onExecuteComplete(T result);
}
