package com.bear.core.command;

import com.bear.core.Event;


public class CommandEvent implements Event {
  private final String command;
  private final boolean sync;
  private final Object[] params;
  private final CommandExecuteHandler handler;

  public CommandEvent(final String command, final CommandExecuteHandler handler) {
    this.command = command;
    this.sync = false;
    this.handler = handler;
    this.params = null;
  }

  public CommandEvent(final String command, final boolean sync, final CommandExecuteHandler handler) {
    this.command = command;
    this.sync = sync;
    this.handler = handler;
    this.params = null;
  }

  public CommandEvent(final String command, final CommandExecuteHandler handler, Object... params) {
    this.command = command;
    this.sync = false;
    this.handler = handler;
    this.params = params;
  }

  public CommandEvent(final String command, final boolean sync, final CommandExecuteHandler handler, Object... params) {
    this.command = command;
    this.sync = sync;
    this.handler = handler;
    this.params = params;
  }

  public String getCommand() {
    return this.command;
  }

  public boolean isSync() {
    return this.sync;
  }

  public CommandExecuteHandler getHandler() {
    return this.handler;
  }

  public Object[] getParams() {
    return params;
  }
}
