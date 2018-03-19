package com.bear.core.config.xml.object.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bear.core.util.Assert;
import com.bear.core.util.ReflectionUtil;
import com.bear.core.util.StatusLogger;
import com.bear.core.util.TypeUtil;



public class TypeConverterRegistry {

  private static final StatusLogger LOGGER = StatusLogger.getLogger();
  private static volatile TypeConverterRegistry INSTANCE;
  private static final Object INSTANCE_LOCK = new Object();

  private final ConcurrentMap<Type, TypeConverter<?>> registry = new ConcurrentHashMap<Type, TypeConverter<?>>();


  public static TypeConverterRegistry getInstance() {
    TypeConverterRegistry result = INSTANCE;
    if (result == null) {
      synchronized (INSTANCE_LOCK) {
        result = INSTANCE;
        if (result == null) {
          INSTANCE = result = new TypeConverterRegistry();
        }
      }
    }
    return result;
  }


  public TypeConverter<?> findCompatibleConverter(final Type type) {
    Assert.requireNonNull(type, "No type was provided");
    final TypeConverter<?> primary = registry.get(type);
    // cached type converters
    if (primary != null) {
      return primary;
    }
    // dynamic enum support
    if (type instanceof Class<?>) {
      final Class<?> clazz = (Class<?>) type;
      if (clazz.isEnum()) {
        @SuppressWarnings({"unchecked","rawtypes"})
        final EnumConverter<? extends Enum> converter = new EnumConverter(clazz.asSubclass(Enum.class));
        registry.putIfAbsent(type, converter);
        return converter;
      }
    }
    // look for compatible converters
    for (final Map.Entry<Type, TypeConverter<?>> entry : registry.entrySet()) {
      final Type key = entry.getKey();
      if (TypeUtil.isAssignable(type, key)) {
        LOGGER.debug("Found compatible TypeConverter<{0}> for type [{1}].", key, type);
        final TypeConverter<?> value = entry.getValue();
        registry.putIfAbsent(type, value);
        return value;
      }
    }
    throw new UnknownFormatConversionException(type.toString());
  }

  private TypeConverterRegistry() {
    LOGGER.debug("TypeConverterRegistry initializing.");
    loadKnownTypeConverters();
    registerPrimitiveTypes();
  }

  private void loadKnownTypeConverters() {
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.BigDecimalConverter.class), ReflectionUtil.instantiate(TypeConverters.BigDecimalConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.BigIntegerConverter.class), ReflectionUtil.instantiate(TypeConverters.BigIntegerConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.BooleanConverter.class), ReflectionUtil.instantiate(TypeConverters.BooleanConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.ByteArrayConverter.class), ReflectionUtil.instantiate(TypeConverters.ByteArrayConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.ByteConverter.class), ReflectionUtil.instantiate(TypeConverters.ByteConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.CharacterConverter.class), ReflectionUtil.instantiate(TypeConverters.CharacterConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.CharArrayConverter.class), ReflectionUtil.instantiate(TypeConverters.CharArrayConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.CharsetConverter.class), ReflectionUtil.instantiate(TypeConverters.CharsetConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.ClassConverter.class), ReflectionUtil.instantiate(TypeConverters.ClassConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.DoubleConverter.class), ReflectionUtil.instantiate(TypeConverters.DoubleConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.FileConverter.class), ReflectionUtil.instantiate(TypeConverters.FileConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.FloatConverter.class), ReflectionUtil.instantiate(TypeConverters.FloatConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.IntegerConverter.class), ReflectionUtil.instantiate(TypeConverters.IntegerConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.LongConverter.class), ReflectionUtil.instantiate(TypeConverters.LongConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.PatternConverter.class), ReflectionUtil.instantiate(TypeConverters.PatternConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.SecurityProviderConverter.class), ReflectionUtil.instantiate(TypeConverters.SecurityProviderConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.ShortConverter.class), ReflectionUtil.instantiate(TypeConverters.ShortConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.StringConverter.class), ReflectionUtil.instantiate(TypeConverters.StringConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.UriConverter.class), ReflectionUtil.instantiate(TypeConverters.UriConverter.class));
    registry.putIfAbsent(getTypeConverterSupportedType(TypeConverters.UrlConverter.class), ReflectionUtil.instantiate(TypeConverters.UrlConverter.class));


  }

  private static Type getTypeConverterSupportedType(@SuppressWarnings("rawtypes") final Class<? extends TypeConverter> typeConverterClass) {
    for (final Type type : typeConverterClass.getGenericInterfaces()) {
      if (type instanceof ParameterizedType) {
        final ParameterizedType pType = (ParameterizedType) type;
        if (TypeConverter.class.equals(pType.getRawType())) {
          // TypeConverter<T> has only one type argument (T), so return that
          return pType.getActualTypeArguments()[0];
        }
      }
    }
    return Void.TYPE;
  }

  private void registerPrimitiveTypes() {
    registerTypeAlias(Boolean.class, Boolean.TYPE);
    registerTypeAlias(Byte.class, Byte.TYPE);
    registerTypeAlias(Character.class, Character.TYPE);
    registerTypeAlias(Double.class, Double.TYPE);
    registerTypeAlias(Float.class, Float.TYPE);
    registerTypeAlias(Integer.class, Integer.TYPE);
    registerTypeAlias(Long.class, Long.TYPE);
    registerTypeAlias(Short.class, Short.TYPE);
  }

  private void registerTypeAlias(final Type knownType, final Type aliasType) {
    registry.putIfAbsent(aliasType, registry.get(knownType));
  }

}
