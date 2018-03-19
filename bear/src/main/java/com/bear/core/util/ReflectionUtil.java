

package com.bear.core.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;


public final class ReflectionUtil {
  private ReflectionUtil() {
  }


  public static <T extends AccessibleObject & Member> boolean isAccessible(final T member) {
    Objects.requireNonNull(member, "No member provided");
    return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers());
  }


  public static <T extends AccessibleObject & Member> void makeAccessible(final T member) {
    if (!isAccessible(member) && !member.isAccessible()) {
      member.setAccessible(true);
    }
  }


  public static void makeAccessible(final Field field) {
    Objects.requireNonNull(field, "No field provided");
    if ((!isAccessible(field) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
      field.setAccessible(true);
    }
  }


  public static Object getFieldValue(final Field field, final Object instance) {
    makeAccessible(field);
    if (!Modifier.isStatic(field.getModifiers())) {
      Objects.requireNonNull(instance, "No instance given for non-static field");
    }
    try {
      return field.get(instance);
    } catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }


  public static Object getStaticFieldValue(final Field field) {
    return getFieldValue(field, null);
  }


  public static void setFieldValue(final Field field, final Object instance, final Object value) {
    makeAccessible(field);
    if (!Modifier.isStatic(field.getModifiers())) {
      Objects.requireNonNull(instance, "No instance given for non-static field");
    }
    try {
      field.set(instance, value);
    } catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }


  public static void setStaticFieldValue(final Field field, final Object value) {
    setFieldValue(field, null, value);
  }


  public static <T> Constructor<T> getDefaultConstructor(final Class<T> clazz) {
    Objects.requireNonNull(clazz, "No class provided");
    try {
      final Constructor<T> constructor = clazz.getDeclaredConstructor();
      makeAccessible(constructor);
      return constructor;
    } catch (final NoSuchMethodException ignored) {
      try {
        final Constructor<T> constructor = clazz.getConstructor();
        makeAccessible(constructor);
        return constructor;
      } catch (final NoSuchMethodException e) {
        throw new IllegalStateException(e);
      }
    }
  }


  public static <T> T instantiate(final Class<T> clazz) {
    Objects.requireNonNull(clazz, "No class provided");
    final Constructor<T> constructor = getDefaultConstructor(clazz);
    try {
      return constructor.newInstance();
    } catch (final LinkageError | InstantiationException e) {
      // LOG4J2-1051
      // On platforms like Google App Engine and Android, some JRE classes are not supported: JMX, JNDI, etc.
      throw new IllegalArgumentException(e);
    } catch (final IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (final InvocationTargetException e) {
      Throwables.rethrow(e.getCause());
      throw new InternalError("Unreachable");
    }
  }

  public static Class<?> getCallerClass(final int depth) {
    if (depth < 0) {
      throw new IndexOutOfBoundsException(Integer.toString(depth));
    }


    // TODO: SecurityManager-based version?
    // slower fallback method using stack trace
    final StackTraceElement element = getEquivalentStackTraceElement(depth + 1);
    try {
      return LoaderUtil.loadClass(element.getClassName());
    } catch (final ClassNotFoundException e) {

    }
    // TODO: return Object.class
    return null;
  }

  static StackTraceElement getEquivalentStackTraceElement(final int depth) {
    // (MS) I tested the difference between using Throwable.getStackTrace() and Thread.getStackTrace(), and
    //      the version using Throwable was surprisingly faster! at least on Java 1.8. See ReflectionBenchmark.
    final StackTraceElement[] elements = new Throwable().getStackTrace();
    int i = 0;
    for (final StackTraceElement element : elements) {
      if (isValid(element)) {
        if (i == depth) {
          return element;
        } else {
          ++i;
        }
      }
    }
    throw new IndexOutOfBoundsException(Integer.toString(depth));
  }

  private static boolean isValid(final StackTraceElement element) {
    // ignore native methods (oftentimes are repeated frames)
    if (element.isNativeMethod()) {
      return false;
    }
    final String cn = element.getClassName();
    // ignore OpenJDK internal classes involved with reflective invocation
    if (cn.startsWith("sun.reflect.")) {
      return false;
    }
    final String mn = element.getMethodName();
    // ignore use of reflection including:
    // Method.invoke
    // InvocationHandler.invoke
    // Constructor.newInstance
    if (cn.startsWith("java.lang.reflect.") && (mn.equals("invoke") || mn.equals("newInstance"))) {
      return false;
    }
    // ignore Class.newInstance
    if (cn.equals("java.lang.Class") && mn.equals("newInstance")) {
      return false;
    }
    // ignore use of Java 1.7+ MethodHandle.invokeFoo() methods
    if (cn.equals("java.lang.invoke.MethodHandle") && mn.startsWith("invoke")) {
      return false;
    }
    // any others?
    return true;
  }
}
