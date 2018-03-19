package com.bear.core.config.xml.object.visitors;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.xml.Node;
import com.bear.core.config.xml.object.ObjectValue;
import com.bear.core.util.StringBuilders;



public class ObjectValueVisitor extends AbstractObjectVisitor<ObjectValue> {
  public ObjectValueVisitor() {
    super(ObjectValue.class);
  }

  @Override
  public Object visit(final Configuration configuration, final Node node, final Event event,
      final StringBuilder log) {
    final String name = this.annotation.value();
    final String rawValue = node.getValue() != null ? node.getValue() :
        removeAttributeValue(node.getAttributes(), "value");
    final String value = this.substitutor.replace(event, rawValue);
    StringBuilders.appendKeyDqValue(log, name, value);
    return value;
  }
}
