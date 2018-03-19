package com.bear.core.config.xml.object.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.xml.Node;
import com.bear.core.config.xml.object.ObjectElement;



public class ObjectElementVisitor extends AbstractObjectVisitor<ObjectElement> {
  public ObjectElementVisitor() {
    super(ObjectElement.class);
  }

  @Override
  public Object visit(final Configuration configuration, final Node node, final Event event,
      final StringBuilder log) {
    final String name = this.annotation.value();
    if (this.conversionType.isArray()) {
      setConversionType(this.conversionType.getComponentType());
      final List<Object> values = new ArrayList<Object>();
      final Collection<Node> used = new ArrayList<Node>();
      log.append("={");
      boolean first = true;
      for (final Node child : node.getChildren()) {
        final Class<?> clazz = child.getClazz();
        if (name.equalsIgnoreCase(child.getName()) || this.conversionType.isAssignableFrom(clazz)) {
          if (!first) {
            log.append(", ");
          }
          first = false;
          used.add(child);
          final Object childObject = child.getObject();
          if (childObject == null) {
            LOGGER.error("Null object returned for {0} in {1}.", child.getName(), node.getName());
            continue;
          }
          if (childObject.getClass().isArray()) {
            log.append(Arrays.toString((Object[]) childObject)).append('}');
            return childObject;
          }
          log.append(child.toString());
          values.add(childObject);
        }
      }
      log.append('}');
      // note that we need to return an empty array instead of null if the types are correct
      if (!values.isEmpty() && !this.conversionType.isAssignableFrom(values.get(0).getClass())) {
        LOGGER.error("Attempted to assign attribute {0} to list of type {1} which is incompatible with {2}.", name,
            values.get(0).getClass(), this.conversionType);
        return null;
      }
      node.getChildren().removeAll(used);
      // we need to use reflection here because values.toArray() will cause type errors at runtime
      final Object[] array = (Object[]) Array.newInstance(this.conversionType, values.size());
      for (int i = 0; i < array.length; i++) {
        array[i] = values.get(i);
      }
      return array;
    }
    final Node namedNode = findNamedNode(name, node.getChildren());
    if (namedNode == null) {
      log.append("null");
      return null;
    }
    log.append(namedNode.getName()).append('(').append(namedNode.toString()).append(')');
    node.getChildren().remove(namedNode);
    return namedNode.getObject();
  }

  private Node findNamedNode(final String name, final Iterable<Node> children) {
    for (final Node child : children) {
      final Class<?> clazz = child.getClazz();
      if (name.equalsIgnoreCase(child.getName()) || this.conversionType.isAssignableFrom(clazz)) {
        // FIXME: check child.getObject() for null?
        // doing so would be more consistent with the array version
        return child;
      }
    }
    return null;
  }
}
