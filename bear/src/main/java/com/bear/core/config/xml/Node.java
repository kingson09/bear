package com.bear.core.config.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Node {

  private final Node parent;
  private final String name;
  private final Class clazz;
  private String value;
  private final Map<String, String> attributes = new HashMap<String, String>();
  private final List<Node> children = new ArrayList<Node>();
  private Object object;



  public Node(final Node parent, final String name, final Class clazz) {
    this.parent = parent;
    this.name = name;
    this.clazz = clazz;
  }

  public Node() {
    this.parent = null;
    this.name = null;
    this.clazz = null;
  }

  public Node(final Node node) {
    this.parent = node.parent;
    this.name = node.name;
    this.clazz = node.clazz;
    this.attributes.putAll(node.getAttributes());
    this.value = node.getValue();
    for (final Node child : node.getChildren()) {
      this.children.add(new Node(child));
    }
    this.object = node.object;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public List<Node> getChildren() {
    return children;
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public Node getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public Class getClazz() {
    return clazz;
  }

  public boolean isRoot() {
    return parent == null;
  }

  public void setObject(final Object obj) {
    object = obj;
  }

  @SuppressWarnings("unchecked")
  public <T> T getObject() {
    return (T) object;
  }


  public <T> T getObject(final Class<T> clazz) {
    return clazz.cast(object);
  }


  public boolean isInstanceOf(final Class<?> clazz) {
    return clazz.isInstance(object);
  }


  @Override
  public String toString() {
    if (object == null) {
      return "null";
    }
    return "name " + name + " with class" + clazz;
  }
}
