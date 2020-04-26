package io.devfactory.domain.projection;

import io.devfactory.domain.Board;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "getOnlyTitle", types = {Board.class})
public interface BoardOnlyContainsTitle {

  String getTitle();

}
