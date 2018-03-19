package com.bear.core.config.xml.object.validation;

import java.lang.annotation.Annotation;



public @interface Constraint {


  Class<? extends ConstraintValidator<? extends Annotation>> value();
}
