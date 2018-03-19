package com.bear.core.config.xml.object.visitors;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.xml.Node;
import com.bear.core.config.xml.object.ObjectConfiguration;



public class ObjectConfigurationVisitor extends AbstractObjectVisitor<ObjectConfiguration> {
  public ObjectConfigurationVisitor() {
    super(ObjectConfiguration.class);
  }

  @Override
  public Object visit(final Configuration configuration, final Node node, final Event event,
      final StringBuilder log) {
    if (this.conversionType.isInstance(configuration)) {
      log.append("Configuration");
      if (configuration.getName() != null) {
        log.append('(').append(configuration.getName()).append(')');
      }
      return configuration;
    }
    LOGGER
        .warn("Variable annotated with @PluginConfiguration is not compatible with type {0}.", configuration.getClass());
    return null;
  }
}
