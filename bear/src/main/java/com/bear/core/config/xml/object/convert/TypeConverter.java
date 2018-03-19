package com.bear.core.config.xml.object.convert;



public interface TypeConverter<T> {


  T convert(String s) throws Exception;
}
