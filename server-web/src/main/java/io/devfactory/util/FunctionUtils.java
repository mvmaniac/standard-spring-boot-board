package io.devfactory.util;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class FunctionUtils {

  public static <T> Supplier<T> emptyEntity(T entity) {
    return () -> entity;
  }

}
