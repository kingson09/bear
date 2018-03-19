package com.bear.core.config.xml.object.visitors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Map;

import com.bear.core.config.xml.object.convert.TypeConverters;
import com.bear.core.lookup.StrSubstitutor;
import com.bear.core.util.Assert;
import com.bear.core.util.StatusLogger;
import com.bear.core.util.Strings;



public abstract class AbstractObjectVisitor<A extends Annotation> implements ObjectVisitor<A> {

  protected static final StatusLogger LOGGER = StatusLogger.getLogger();

  protected final Class<A> clazz;
  protected A annotation;
  protected String[] aliases;
  protected Class<?> conversionType;
  protected StrSubstitutor substitutor;
  protected Member member;


  protected AbstractObjectVisitor(final Class<A> clazz) {
    this.clazz = clazz;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ObjectVisitor<A> setAnnotation(final Annotation annotation) {
    final Annotation a = Assert.requireNonNull(annotation, "No annotation was provided");
    if (this.clazz.isInstance(a)) {
      this.annotation = (A) a;
    }
    return this;
  }

  @Override
  public ObjectVisitor<A> setAliases(final String... aliases) {
    this.aliases = aliases;
    return this;
  }

  @Override
  public ObjectVisitor<A> setConversionType(final Class<?> conversionType) {
    this.conversionType = Assert.requireNonNull(conversionType, "No conversion type class was provided");
    return this;
  }

  @Override
  public ObjectVisitor<A> setStrSubstitutor(final StrSubstitutor substitutor) {
    this.substitutor = Assert.requireNonNull(substitutor, "No StrSubstitutor was provided");
    return this;
  }

  @Override
  public ObjectVisitor<A> setMember(final Member member) {
    this.member = member;
    return this;
  }


  protected static String removeAttributeValue(final Map<String, String> attributes, final String name,
      final String... aliases) {
    for (final Map.Entry<String, String> entry : attributes.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      if (key.equalsIgnoreCase(name)) {
        attributes.remove(key);
        return value;
      }
      if (aliases != null) {
        for (final String alias : aliases) {
          if (key.equalsIgnoreCase(alias)) {
            attributes.remove(key);
            return value;
          }
        }
      }
    }
    return null;
  }

  protected Object convert(final String value, final Object defaultValue) {
    if (defaultValue instanceof String) {
      return TypeConverters.convert(value, this.conversionType, Strings.trimToNull((String) defaultValue));
    }
    return TypeConverters.convert(value, this.conversionType, defaultValue);
  }
}
