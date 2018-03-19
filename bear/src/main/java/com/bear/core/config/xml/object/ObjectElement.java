package com.bear.core.config.xml.object;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bear.core.config.xml.object.visitors.ObjectElementVisitor;
import com.bear.core.config.xml.object.visitors.ObjectVisitorStrategy;



@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD})
@ObjectVisitorStrategy(ObjectElementVisitor.class)
public @interface ObjectElement {


  String value();
}