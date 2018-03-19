package com.bear.core.config.xml.object.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import com.bear.core.Event;
import com.bear.core.config.Configuration;
import com.bear.core.config.ConfigurationException;
import com.bear.core.config.xml.Node;
import com.bear.core.config.xml.object.ObjectAliases;
import com.bear.core.config.xml.object.ObjectBuilderFactory;
import com.bear.core.config.xml.object.ObjectFactory;
import com.bear.core.config.xml.object.validation.ConstraintValidator;
import com.bear.core.config.xml.object.validation.ConstraintValidators;
import com.bear.core.config.xml.object.visitors.ObjectVisitor;
import com.bear.core.config.xml.object.visitors.ObjectVisitors;
import com.bear.core.util.Assert;
import com.bear.core.util.Builder;
import com.bear.core.util.ReflectionUtil;
import com.bear.core.util.StatusLogger;
import com.bear.core.util.StringBuilders;
import com.bear.core.util.TypeUtil;



public class ObjectBuilder implements Builder<Object> {

  private static final StatusLogger LOGGER = StatusLogger.getLogger();

  private final Class<?> clazz;

  private Configuration configuration;
  private Node node;
  private Event event;


  public ObjectBuilder(Class<?> clazz) {
    this.clazz = clazz;
  }

  public ObjectBuilder withConfiguration(final Configuration configuration) {
    this.configuration = configuration;
    return this;
  }


  public ObjectBuilder withConfigurationNode(final Node node) {
    this.node = node;
    return this;
  }


  public ObjectBuilder forEvent(final Event event) {
    this.event = event;
    return this;
  }


  @Override
  public Object build() {
    verify();
    // first try to use a builder class if one is available
    try {
      LOGGER.debug("Building Object[class={0}]. Searching for builder factory method...", clazz.getName());
      final Builder<?> builder = createBuilder(this.clazz);
      if (builder != null) {
        injectFields(builder);
        final Object result = builder.build();
        LOGGER.debug("Built Object[name={0}] OK from builder factory method.", clazz);
        return result;
      }
    } catch (final Exception e) {
      LOGGER.error("Unable to inject fields into builder class for Object type {0}, element {1}.", this.clazz,
          node.getName(), e);
    }
    // or fall back to factory method if no builder class is available
    try {
      LOGGER.debug("Still building Object[name={0}, class={1}]. Searching for factory method...", clazz.getName());
      final Method factory = findFactoryMethod(this.clazz);
      final Object[] params = generateParameters(factory);
      final Object Object = factory.invoke(null, params);
      LOGGER.debug("Built Object[name={0}] OK from factory method.", clazz);
      return Object;
    } catch (final Exception e) {
      LOGGER.error("Unable to invoke factory method in class {0} for element {1}.", this.clazz, this.node.getName(), e);
      return null;
    }
  }

  private void verify() {
    Assert.requireNonNull(this.configuration, "No Configuration object was set.");
    Assert.requireNonNull(this.node, "No Node object was set.");
  }

  private static Builder<?> createBuilder(final Class<?> clazz) throws InvocationTargetException,
      IllegalAccessException {
    for (final Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(ObjectBuilderFactory.class) &&
          Modifier.isStatic(method.getModifiers()) &&
          TypeUtil.isAssignable(Builder.class, method.getGenericReturnType())) {
        ReflectionUtil.makeAccessible(method);
        @SuppressWarnings("unchecked") final Builder<?> builder = (Builder<?>) method.invoke(null);
        LOGGER.debug("Found builder factory method [{0}]: {1}.", method.getName(), method);
        return builder;
      }
    }
    LOGGER.debug("No builder factory method found in class {0}. Going to try finding a factory method instead.",
        clazz.getName());
    return null;
  }

  private void injectFields(final Builder<?> builder) throws IllegalAccessException {
    final Field[] fields = builder.getClass().getDeclaredFields();
    AccessibleObject.setAccessible(fields, true);
    final StringBuilder log = new StringBuilder();
    boolean invalid = false;
    for (final Field field : fields) {
      log.append(log.length() == 0 ? "with params(" : ", ");
      final Annotation[] annotations = field.getDeclaredAnnotations();
      final String[] aliases = extractObjectAliases(annotations);
      for (final Annotation a : annotations) {
        if (a instanceof ObjectAliases) {
          continue; // already processed
        }
        final ObjectVisitor<? extends Annotation> visitor = ObjectVisitors.findVisitor(a.annotationType());
        if (visitor != null) {
          final Object value =
              visitor.setAliases(aliases).setAnnotation(a).setConversionType(field.getType()).setMember(field)
                  .visit(configuration, node, event, log);
          // don't overwrite default values if the visitor gives us no value to inject
          if (value != null) {
            field.set(builder, value);
          }
        }
      }
      final Collection<ConstraintValidator<?>> validators = ConstraintValidators.findValidators(annotations);
      final Object value = field.get(builder);
      for (final ConstraintValidator<?> validator : validators) {
        if (!validator.isValid(value)) {
          invalid = true;
        }
      }
    }
    if (log.length() > 0) {
      log.append(')');
    }
    LOGGER.debug("Calling build() on class {0} for element {1} {2}", builder.getClass(), node.getName(), log.toString());
    if (invalid) {
      throw new ConfigurationException("Arguments given for element " + node.getName() + " are invalid");
    }
    checkForRemainingAttributes();
  }

  private static Method findFactoryMethod(final Class<?> clazz) {
    for (final Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(ObjectFactory.class) && Modifier.isStatic(method.getModifiers())) {
        LOGGER.debug("Found factory method [{0}]: {1}.", method.getName(), method);
        ReflectionUtil.makeAccessible(method);
        return method;
      }
    }
    throw new IllegalStateException("No factory method found for class " + clazz.getName());
  }

  private Object[] generateParameters(final Method factory) {
    final StringBuilder log = new StringBuilder();
    final Class<?>[] types = factory.getParameterTypes();
    final Annotation[][] annotations = factory.getParameterAnnotations();
    final Object[] args = new Object[annotations.length];
    boolean invalid = false;
    for (int i = 0; i < annotations.length; i++) {
      log.append(log.length() == 0 ? "with params(" : ", ");
      final String[] aliases = extractObjectAliases(annotations[i]);
      for (final Annotation a : annotations[i]) {
        if (a instanceof ObjectAliases) {
          continue; // already processed
        }
        final ObjectVisitor<? extends Annotation> visitor = ObjectVisitors.findVisitor(a.annotationType());
        if (visitor != null) {
          final Object value = visitor.setAliases(aliases).setAnnotation(a).setConversionType(types[i])
              .setStrSubstitutor(configuration.getStrSubstitutor()).setMember(factory)
              .visit(configuration, node, event, log);
          // don't overwrite existing values if the visitor gives us no value to inject
          if (value != null) {
            args[i] = value;
          }
        }
      }
      final Collection<ConstraintValidator<?>> validators = ConstraintValidators.findValidators(annotations[i]);
      final Object value = args[i];
      for (final ConstraintValidator<?> validator : validators) {
        if (!validator.isValid(value)) {
          invalid = true;
        }
      }
    }
    if (log.length() > 0) {
      log.append(')');
    }
    checkForRemainingAttributes();
    LOGGER.debug("Calling {0} on class {1} for element {2} {3}", factory.getName(), clazz.getName(), node.getName(),
        log.toString());
    if (invalid) {
      throw new ConfigurationException("Arguments given for element " + node.getName() + " are invalid");
    }
    return args;
  }

  private static String[] extractObjectAliases(final Annotation... parmTypes) {
    String[] aliases = null;
    for (final Annotation a : parmTypes) {
      if (a instanceof ObjectAliases) {
        aliases = ((ObjectAliases) a).value();
      }
    }
    return aliases;
  }

  private void checkForRemainingAttributes() {
    final Map<String, String> attrs = node.getAttributes();
    if (!attrs.isEmpty()) {
      final StringBuilder sb = new StringBuilder();
      for (final String key : attrs.keySet()) {
        if (sb.length() == 0) {
          sb.append(node.getName());
          sb.append(" contains ");
          if (attrs.size() == 1) {
            sb.append("an invalid element or attribute ");
          } else {
            sb.append("invalid attributes ");
          }
        } else {
          sb.append(", ");
        }
        StringBuilders.appendDqValue(sb, key);

      }
      LOGGER.error(sb.toString());
    }
  }

}
