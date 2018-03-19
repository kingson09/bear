package com.bear.core.async;



public interface EventHandler<T> {
  void onEvent(T event);
}
