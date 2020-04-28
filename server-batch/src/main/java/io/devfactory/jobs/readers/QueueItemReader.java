package io.devfactory.jobs.readers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.springframework.batch.item.ItemReader;

public class QueueItemReader<T> implements ItemReader<T> {

  private final Queue<T> queue;

  public QueueItemReader(List<T> data) {
    this.queue = new LinkedList<>(data);
  }

  @Override
  public T read() {
    return this.queue.poll();
  }

}
