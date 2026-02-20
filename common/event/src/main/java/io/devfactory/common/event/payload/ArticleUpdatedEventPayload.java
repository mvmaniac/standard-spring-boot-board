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
public class ArticleUpdatedEventPayload implements EventPayload {

  private Long articleId;
  private String title;
  private String content;
  private Long boardId;
  private Long writerId;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

}
