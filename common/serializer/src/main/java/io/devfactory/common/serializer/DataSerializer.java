package io.devfactory.common.serializer;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class DataSerializer {

  private static final ObjectMapper objectMapper = initializer();

  private static ObjectMapper initializer() {
    return JsonMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .build();
  }

  public static <T> T deserialize(String data, Class<T> clazz) {
    try {
      return objectMapper.readValue(data, clazz);
    } catch (JacksonException e) {
      log.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, e);
      return null;
    }
  }

  public static <T> T deserialize(Object data, Class<T> clazz) {
    return objectMapper.convertValue(data, clazz);
  }

  public static String serialize(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JacksonException e) {
      log.error("[DataSerializer.serialize] object={}", object, e);
      return null;
    }
  }

}
