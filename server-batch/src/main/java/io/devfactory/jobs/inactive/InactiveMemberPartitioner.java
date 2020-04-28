package io.devfactory.jobs.inactive;

import io.devfactory.domain.enums.Grade;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import java.util.HashMap;
import java.util.Map;

public class InactiveMemberPartitioner implements Partitioner {

  private static final String GRADE = "grade";
  private static final String INACTIVE_MEMBER_TASK = "inactiveMemberTask";

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {

    Map<String, ExecutionContext> map = new HashMap<>(gridSize);
    final Grade[] grades = Grade.values();

    for (int i = 0, length = grades.length; i < length; i++) {
      final ExecutionContext context = new ExecutionContext();
      context.putString(GRADE, grades[i].name());
      map.put(INACTIVE_MEMBER_TASK + i, context);
    }

    return map;
  }

}
