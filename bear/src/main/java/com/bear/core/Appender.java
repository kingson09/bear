package com.bear.core;


public interface Appender<E extends Event> {

  String ELEMENT_TYPE = "appender";

  /**
   * @param event this appender will accept a event,this event will be a data Event or a command event
   */

  void accept(Event event);

  void append(E event);


  String getName();


  Formatter<E, ?> getFormatter();


  boolean ignoreExceptions();


  ErrorHandler getHandler();


  void setHandler(ErrorHandler handler);
}
