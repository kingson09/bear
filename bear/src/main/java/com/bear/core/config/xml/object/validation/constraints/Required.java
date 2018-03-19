package com.bear.core.config.xml.object.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bear.core.config.xml.object.validation.Constraint;
import com.bear.core.config.xml.object.validation.validators.RequiredValidator;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Constraint(RequiredValidator.class)
public @interface Required {


  String message() default "The parameter is null";
}
