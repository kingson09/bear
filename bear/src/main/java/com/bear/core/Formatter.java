package com.bear.core;



public interface Formatter<E extends Event,F> {
  F format(E event);
}
