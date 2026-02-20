package io.devfactory.common.event.payload;

import io.devfactory.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikedEventPayload implements EventPayload {

  private Long articleLikeId;
  private Long articleId;
  private Long userId;
  private LocalDateTime createdAt;
  private Long articleLikeCount;

}
