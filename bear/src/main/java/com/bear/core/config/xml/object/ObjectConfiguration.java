package com.bear.core.config.xml.object;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bear.core.config.xml.object.visitors.ObjectConfigurationVisitor;
import com.bear.core.config.xml.object.visitors.ObjectVisitorStrategy;



@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD})
@ObjectVisitorStrategy(ObjectConfigurationVisitor.class)
public @interface ObjectConfiguration {
}
