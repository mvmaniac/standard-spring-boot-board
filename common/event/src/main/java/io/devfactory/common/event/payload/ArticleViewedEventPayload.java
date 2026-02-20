package io.devfactory.common.event.payload;

import io.devfactory.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleViewedEventPayload implements EventPayload {

  private Long articleId;
  private Long articleViewCount;

}
