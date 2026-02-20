package io.devfactory.common.event;

import io.devfactory.common.serializer.DataSerializer;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

@Getter
public class Event<T extends EventPayload> {

  private Long eventId;
  private EventType eventType;
  private T payload;

  public static Event<EventPayload> of(Long eventId, EventType eventType, EventPayload payload) {
    final var event = new Event<EventPayload>();
    event.eventId = eventId;
    event.eventType = eventType;
    event.payload = payload;
    return event;
  }

  public String toJson() {
    return DataSerializer.serialize(this);
  }

  @NonNull
  public static Event<EventPayload> fromJson(String json) {
    final var eventRaw = Objects.requireNonNull(DataSerializer.deserialize(json, EventRaw.class));

    final var event = new Event<EventPayload>();
    event.eventId = Objects.requireNonNull(eventRaw.getEventId());
    event.eventType = Objects.requireNonNull(EventType.from(eventRaw.getEventType()));
    event.payload = Objects.requireNonNull(DataSerializer.deserialize(eventRaw.getPayload(), event.eventType.getPayloadClass()));
    return event;
  }

  @Getter
  private static class EventRaw {
    private Long eventId;
    private String eventType;
    private Object payload;
  }

}
