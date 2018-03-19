package com.bear.core.config.xml.object.visitors;

import java.util.Map;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.xml.Node;
import com.bear.core.config.xml.object.ObjectAttribute;
import com.bear.core.util.StringBuilders;



public class ObjectAttributeVisitor  extends AbstractObjectVisitor<ObjectAttribute> {
  public ObjectAttributeVisitor() {
    super(ObjectAttribute.class);
  }

  @Override
  public Object visit(final Configuration configuration, final Node node, final Event event,
      final StringBuilder log) {
    final String name = this.annotation.value();
    final Map<String, String> attributes = node.getAttributes();
    final String rawValue = removeAttributeValue(attributes, name, this.aliases);
    final String replacedValue = this.substitutor.replace(event, rawValue);
    final Object defaultValue = findDefaultValue(event);
    final Object value = convert(replacedValue, defaultValue);
    final Object debugValue =value;
    StringBuilders.appendKeyDqValue(log, "name", debugValue);
    return value;
  }

  private Object findDefaultValue(final Event event) {
    if (this.conversionType == int.class || this.conversionType == Integer.class) {
      return this.annotation.defaultInt();
    }
    if (this.conversionType == long.class || this.conversionType == Long.class) {
      return this.annotation.defaultLong();
    }
    if (this.conversionType == boolean.class || this.conversionType == Boolean.class) {
      return this.annotation.defaultBoolean();
    }
    if (this.conversionType == float.class || this.conversionType == Float.class) {
      return this.annotation.defaultFloat();
    }
    if (this.conversionType == double.class || this.conversionType == Double.class) {
      return this.annotation.defaultDouble();
    }
    if (this.conversionType == byte.class || this.conversionType == Byte.class) {
      return this.annotation.defaultByte();
    }
    if (this.conversionType == char.class || this.conversionType == Character.class) {
      return this.annotation.defaultChar();
    }
    if (this.conversionType == short.class || this.conversionType == Short.class) {
      return this.annotation.defaultShort();
    }
    if (this.conversionType == Class.class) {
      return this.annotation.defaultClass();
    }
    return this.substitutor.replace(event, this.annotation.defaultString());
  }
}
