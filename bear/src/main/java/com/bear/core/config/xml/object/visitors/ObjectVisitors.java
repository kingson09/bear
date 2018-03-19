package com.bear.core.config.xml.object.visitors;

import java.lang.annotation.Annotation;

import com.bear.core.util.StatusLogger;


public final class ObjectVisitors {
  private static final StatusLogger LOGGER = StatusLogger.getLogger();

  private ObjectVisitors() {
  }


  public static ObjectVisitor<? extends Annotation> findVisitor(final Class<? extends Annotation> annotation) {
    final ObjectVisitorStrategy strategy = annotation.getAnnotation(ObjectVisitorStrategy.class);
    try {
      if (strategy == null) {
        LOGGER.debug("No PluginVisitorStrategy found on annotation [{0}]. Ignoring.", annotation);
        return findStrategy(annotation);
      }

      return strategy.value().newInstance();
    } catch (final Exception e) {
      LOGGER.error("Error loading PluginVisitor [{0}] for annotation [{1}].", strategy.value(), annotation, e);
      return null;
    }
  }

  private static ObjectVisitor<? extends Annotation> findStrategy(final Class<? extends Annotation> annotation) throws
      IllegalAccessException, InstantiationException {
    switch (annotation.getSimpleName()) {
      case "ObjectAttribute":
        return ObjectAttributeVisitor.class.newInstance();
      case "ObjectConfiguration":
        return ObjectConfigurationVisitor.class.newInstance();
      case "ObjectElement":
        return ObjectElementVisitor.class.newInstance();
      case "ObjectValue":
        return ObjectValueVisitor.class.newInstance();
    }
    return null;
  }
}
