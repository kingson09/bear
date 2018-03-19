package com.bear.core.config.xml.object.visitors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.xml.Node;
import com.bear.core.lookup.StrSubstitutor;



public interface ObjectVisitor<A extends Annotation> {


  ObjectVisitor<A> setAnnotation(Annotation annotation);


  ObjectVisitor<A> setAliases(String... aliases);


  ObjectVisitor<A> setConversionType(Class<?> conversionType);

  ObjectVisitor<A> setStrSubstitutor(StrSubstitutor substitutor);

  ObjectVisitor<A> setMember(Member member);


  Object visit(Configuration configuration, Node node, Event event, StringBuilder log);
}
