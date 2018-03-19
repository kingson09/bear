package com.bear.core.config.xml.object.convert;

import com.bear.core.util.EnglishEnums;



public class EnumConverter<E extends Enum<E>> implements TypeConverter<E> {
  private final Class<E> clazz;

  public EnumConverter(final Class<E> clazz) {
    this.clazz = clazz;
  }

  @Override
  public E convert(final String s) {
    return EnglishEnums.valueOf(clazz, s);
  }
}
