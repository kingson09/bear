package com.bear.core.async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bear.core.util.StatusLogger;


public class Disruptor<T> implements Runnable {
  private StatusLogger log = StatusLogger.getLogger();
  private final BlockingQueue<T> queue;
  private Thread executor;
  private final AtomicBoolean started;
  private EventHandler<T> eventHandler;
  private ExceptionHandler exceptionHandler;

  public Disruptor(BlockingQueue<T> queue) {
    this.started = new AtomicBoolean(false);
    this.queue = queue;
  }

  public void handleEventsWith(EventHandler<T> handler) {
    this.eventHandler = handler;
  }

  public void handleExceptionsWith(ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  public void start() {
    if (started.compareAndSet(false, true)) {
      executor = new Thread(this, "bear");
      executor.start();
    }
  }

  public void shutdown(boolean immediate, long delayMilliseconds) {
    if (started.compareAndSet(true, false)) {
      log.debug("disruptor queue size:" + queue.size());
      if (immediate) {
        queue.clear();
      }
      executor.interrupt();
      try {
        executor.join(delayMilliseconds);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void shutdown(boolean immediate) {
    if (started.compareAndSet(true, false)) {
      if (immediate) {
        queue.clear();
      }
      executor.interrupt();
      try {
        executor.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void publishEvent(T event) {
    if (started.get() && !queue.offer(event) && exceptionHandler != null) {
      exceptionHandler.handleException(new EventQueueBlockedException());
    }

  }

  @Override
  public void run() {
    while (started.get() || !queue.isEmpty()) {
      try {
        T event = queue.take();
        if (eventHandler != null) {
          eventHandler.onEvent(event);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }
}
