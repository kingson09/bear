package com.bear.core.config.xml.object.visitors;

import java.lang.annotation.Annotation;



public @interface ObjectVisitorStrategy {

  Class<? extends ObjectVisitor<? extends Annotation>> value();
}