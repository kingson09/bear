package com.bear.core.config.xml.object.validation.validators;

import java.util.Collection;
import java.util.Map;

import com.bear.core.config.xml.object.validation.ConstraintValidator;
import com.bear.core.config.xml.object.validation.constraints.Required;
import com.bear.core.util.StatusLogger;



public class RequiredValidator implements ConstraintValidator<Required> {

  private static final StatusLogger LOGGER = StatusLogger.getLogger();

  private Required annotation;

  @Override
  public void initialize(final Required annotation) {
    this.annotation = annotation;
  }

  @Override
  public boolean isValid(final Object value) {
    if (value == null) {
      return err();
    }
    if (value instanceof CharSequence) {
      final CharSequence sequence = (CharSequence) value;
      return sequence.length() != 0 || err();
    }
    final Class<?> clazz = value.getClass();
    if (clazz.isArray()) {
      final Object[] array = (Object[]) value;
      return array.length != 0 || err();
    }
    if (Collection.class.isAssignableFrom(clazz)) {
      final Collection<?> collection = (Collection<?>) value;
      return collection.size() != 0 || err();
    }
    if (Map.class.isAssignableFrom(clazz)) {
      final Map<?, ?> map = (Map<?, ?>) value;
      return map.size() != 0 || err();
    }
    LOGGER.debug("Encountered type [{0}] which can only be checked for null.", clazz.getName());
    return true;
  }

  private boolean err() {
    LOGGER.error(annotation.message());
    return false;
  }
}