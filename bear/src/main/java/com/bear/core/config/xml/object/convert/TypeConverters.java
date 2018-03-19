package com.bear.core.config.xml.object.convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Provider;
import java.security.Security;
import java.util.regex.Pattern;

import com.bear.core.util.LoaderUtil;
import com.bear.core.util.StatusLogger;



public final class TypeConverters {

  public static class BigDecimalConverter implements TypeConverter<BigDecimal> {
    @Override
    public BigDecimal convert(final String s) {
      return new BigDecimal(s);
    }
  }

  public static class BigIntegerConverter implements TypeConverter<BigInteger> {
    @Override
    public BigInteger convert(final String s) {
      return new BigInteger(s);
    }
  }

  public static class BooleanConverter implements TypeConverter<Boolean> {
    @Override
    public Boolean convert(final String s) {
      return Boolean.valueOf(s);
    }
  }

  public static class ByteArrayConverter implements TypeConverter<byte[]> {

    private static final String PREFIX_0x = "0x";
    private static final String PREFIX_BASE64 = "Base64:";

    @Override
    public byte[] convert(final String value) {
      byte[] bytes;
      if (value == null || value.isEmpty()) {
        bytes = new byte[0];
      } else if (value.startsWith(PREFIX_BASE64)) {
        final String lexicalXSDBase64Binary = value.substring(PREFIX_BASE64.length());
        bytes = Base64Converter.parseBase64Binary(lexicalXSDBase64Binary);
      } else if (value.startsWith(PREFIX_0x)) {
        final String lexicalXSDHexBinary = value.substring(PREFIX_0x.length());
        bytes = HexConverter.parseHexBinary(lexicalXSDHexBinary);
      } else {
        bytes = value.getBytes(Charset.defaultCharset());
      }
      return bytes;
    }
  }

  public static class ByteConverter implements TypeConverter<Byte> {
    @Override
    public Byte convert(final String s) {
      return Byte.valueOf(s);
    }
  }

  public static class CharacterConverter implements TypeConverter<Character> {
    @Override
    public Character convert(final String s) {
      if (s.length() != 1) {
        throw new IllegalArgumentException("Character string must be of length 1: " + s);
      }
      return Character.valueOf(s.toCharArray()[0]);
    }
  }

  public static class CharArrayConverter implements TypeConverter<char[]> {
    @Override
    public char[] convert(final String s) {
      return s.toCharArray();
    }
  }

  public static class CharsetConverter implements TypeConverter<Charset> {
    @Override
    public Charset convert(final String s) {
      return Charset.forName(s);
    }
  }


  public static class ClassConverter implements TypeConverter<Class<?>> {
    @Override
    public Class<?> convert(final String s) throws ClassNotFoundException {
      return LoaderUtil.loadClass(s);
    }
  }

  public static class DoubleConverter implements TypeConverter<Double> {
    @Override
    public Double convert(final String s) {
      return Double.valueOf(s);
    }
  }

  public static class FileConverter implements TypeConverter<File> {
    @Override
    public File convert(final String s) {
      return new File(s);
    }
  }

  public static class FloatConverter implements TypeConverter<Float> {
    @Override
    public Float convert(final String s) {
      return Float.valueOf(s);
    }
  }


  public static class IntegerConverter implements TypeConverter<Integer> {
    @Override
    public Integer convert(final String s) {
      return Integer.valueOf(s);
    }
  }


  public static class LongConverter implements TypeConverter<Long> {
    @Override
    public Long convert(final String s) {
      return Long.valueOf(s);
    }
  }

  public static class PatternConverter implements TypeConverter<Pattern> {
    @Override
    public Pattern convert(final String s) {
      return Pattern.compile(s);
    }
  }

  public static class SecurityProviderConverter implements TypeConverter<Provider> {
    @Override
    public Provider convert(final String s) {
      return Security.getProvider(s);
    }
  }

  public static class ShortConverter implements TypeConverter<Short> {
    @Override
    public Short convert(final String s) {
      return Short.valueOf(s);
    }
  }

  public static class StringConverter implements TypeConverter<String> {
    @Override
    public String convert(final String s) {
      return s;
    }
  }

  public static class UriConverter implements TypeConverter<URI> {
    @Override
    public URI convert(final String s) throws URISyntaxException {
      return new URI(s);
    }
  }


  public static class UrlConverter implements TypeConverter<URL> {
    @Override
    public URL convert(final String s) throws MalformedURLException {
      return new URL(s);
    }
  }


  public static Object convert(final String s, final Class<?> clazz, final Object defaultValue) {
    final TypeConverter<?> converter = TypeConverterRegistry.getInstance().findCompatibleConverter(clazz);
    if (s == null) {
      // don't debug print here, resulting output is hard to understand
      // LOGGER.debug("Null string given to convert. Using default [{}].", defaultValue);
      return parseDefaultValue(converter, defaultValue);
    }
    try {
      return converter.convert(s);
    } catch (final Exception e) {
      LOGGER.warn("Error while converting string [{0}] to type [{1}]. Using default value [{2}].", s, clazz, defaultValue,
          e);
      return parseDefaultValue(converter, defaultValue);
    }
  }

  private static Object parseDefaultValue(final TypeConverter<?> converter, final Object defaultValue) {
    if (defaultValue == null) {
      return null;
    }
    if (!(defaultValue instanceof String)) {
      return defaultValue;
    }
    try {
      return converter.convert((String) defaultValue);
    } catch (final Exception e) {
      LOGGER.debug("Can't parse default value [{0}] for type [{1}].", defaultValue, converter.getClass(), e);
      return null;
    }
  }

  private static final StatusLogger LOGGER = StatusLogger.getLogger();
}
