package com.yishun.log.weaver;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by bjliuzhanyong on 2017/9/27.
 */

@Target({ TYPE, METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
public @interface MethodLog {
  boolean logParam() default false;

  boolean calTime() default false;
}

