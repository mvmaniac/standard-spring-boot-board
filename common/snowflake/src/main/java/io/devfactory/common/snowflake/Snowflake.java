package io.devfactory.common.snowflake;

import java.util.function.LongSupplier;
import java.util.random.RandomGenerator;

public class Snowflake {

  private static final int UNUSED_BITS = 1;
  private static final int EPOCH_BITS = 41;
  private static final int NODE_ID_BITS = 10;
  private static final int SEQUENCE_BITS = 12;

  private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
  private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

  private static final long START_TIME_MILLIS = 1704067200000L; // UTC = 2024-01-01T00:00:00Z

  private final long nodeId;
  private final LongSupplier timeSource;

  private long lastTimeMillis = START_TIME_MILLIS;
  private long sequence = 0L;

  public Snowflake() {
    this(RandomGenerator.getDefault().nextLong(MAX_NODE_ID + 1), System::currentTimeMillis);
  }

  public Snowflake(long nodeId, LongSupplier timeSource) {
    if (nodeId < 0 || nodeId > MAX_NODE_ID) {
      throw new IllegalArgumentException("nodeId out of range: " + nodeId);
    }
    this.nodeId = nodeId;
    this.timeSource = timeSource;
  }

  public synchronized long nextId() {
    long currentTimeMillis = timeSource.getAsLong();

    if (currentTimeMillis < lastTimeMillis) {
      throw new IllegalStateException(
        "System clock moved backwards: current=" + currentTimeMillis + ", last=" + lastTimeMillis
      );
    }

    if (currentTimeMillis == lastTimeMillis) {
      sequence = (sequence + 1) & MAX_SEQUENCE;
      if (sequence == 0) {
        currentTimeMillis = this.waitNextMillis(currentTimeMillis);
      }
    } else {
      sequence = 0;
    }

    lastTimeMillis = currentTimeMillis;

    return ((currentTimeMillis - START_TIME_MILLIS) << (NODE_ID_BITS + SEQUENCE_BITS))
      | (nodeId << SEQUENCE_BITS)
      | sequence;
  }

  private long waitNextMillis(long currentTimestamp) {
    while (currentTimestamp <= lastTimeMillis) {
      Thread.onSpinWait();
      currentTimestamp = timeSource.getAsLong();
    }
    return currentTimestamp;
  }

}
