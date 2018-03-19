package com.bear.core.config.xml.object.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.bear.core.util.ReflectionUtil;



public final class ConstraintValidators {

  private ConstraintValidators() {
  }


  public static Collection<ConstraintValidator<?>> findValidators(final Annotation... annotations) {
    final Collection<ConstraintValidator<?>> validators =
        new ArrayList<ConstraintValidator<?>>();
    for (final Annotation annotation : annotations) {
      final Class<? extends Annotation> type = annotation.annotationType();
      if (type.isAnnotationPresent(Constraint.class)) {
        final ConstraintValidator<?> validator = getValidator(annotation, type);
        if (validator != null) {
          validators.add(validator);
        }
      }
    }
    return validators;
  }

  private static <A extends Annotation> ConstraintValidator<A> getValidator(final A annotation,
      final Class<? extends A> type) {
    final Constraint constraint = type.getAnnotation(Constraint.class);
    final Class<? extends ConstraintValidator<?>> validatorClass = constraint.value();
    if (type.equals(getConstraintValidatorAnnotationType(validatorClass))) {
      @SuppressWarnings("unchecked") // I don't think we could be any more thorough in validation here
      final ConstraintValidator<A> validator = (ConstraintValidator<A>)
          ReflectionUtil.instantiate(validatorClass);
      validator.initialize(annotation);
      return validator;
    }
    return null;
  }

  private static Type getConstraintValidatorAnnotationType(final Class<? extends ConstraintValidator<?>> type) {
    for (final Type parentType : type.getGenericInterfaces()) {
      if (parentType instanceof ParameterizedType) {
        final ParameterizedType parameterizedType = (ParameterizedType) parentType;
        if (ConstraintValidator.class.equals(parameterizedType.getRawType())) {
          return parameterizedType.getActualTypeArguments()[0];
        }
      }
    }
    return Void.TYPE;
  }
}