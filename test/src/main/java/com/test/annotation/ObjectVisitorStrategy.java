package com.test.annotation;

public @interface ObjectVisitorStrategy {

  Class<?> value() default String.class;
}