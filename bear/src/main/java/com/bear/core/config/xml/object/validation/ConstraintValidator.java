package com.bear.core.config.xml.object.validation;

import java.lang.annotation.Annotation;



public interface ConstraintValidator<A extends Annotation> {


  void initialize(A annotation);


  boolean isValid(Object value);
}