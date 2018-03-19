package com.bear.core.config.xml.object;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bear.core.config.xml.object.visitors.ObjectAttributeVisitor;
import com.bear.core.config.xml.object.visitors.ObjectVisitorStrategy;
import com.bear.core.util.Strings;



@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD})
@ObjectVisitorStrategy(ObjectAttributeVisitor.class)
public @interface ObjectAttribute {


  boolean defaultBoolean() default false;


  byte defaultByte() default 0;


  char defaultChar() default 0;


  Class<?> defaultClass() default Object.class;


  double defaultDouble() default 0.0d;


  float defaultFloat() default 0.0f;


  int defaultInt() default 0;


  long defaultLong() default 0L;


  short defaultShort() default 0;


  String defaultString() default Strings.EMPTY;

  // TODO: could we allow a blank value and infer the attribute name through reflection?

  String value();

}
