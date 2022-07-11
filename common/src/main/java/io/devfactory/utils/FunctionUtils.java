package io.devfactory.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;


@UtilityClass
public class FunctionUtils {

  public static final UnaryOperator<String> redirect = path -> "redirect:" + path;

  public static <T> Supplier<T> emptyEntity(T entity) {
    return () -> entity;
  }

}
