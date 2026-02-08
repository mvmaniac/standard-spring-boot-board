package io.devfactory.comment.entity;


import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CommentPath {

  private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private static final int DEPTH_CHUNK_SIZE = 5;
  private static final int MAX_DEPTH = 5;

  // MIN_CHUNK = "00000", MAX_CHUNK = "zzzzz"
  private static final String MIN_CHUNK = String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
  private static final String MAX_CHUNK = String.valueOf(CHARSET.charAt(CHARSET.length() - 1)).repeat(DEPTH_CHUNK_SIZE);

  private String path;

  public static CommentPath create(String path) {
    if (isDepthOverflowed(path)) throw new IllegalStateException("depth overflowed");

    final var commentPath = new CommentPath();
    commentPath.path = path;
    return commentPath;
  }

  private static boolean isDepthOverflowed(String path) {
    return CommentPath.calDepth(path) > MAX_DEPTH;
  }

  private static int calDepth(String path) {
    return path.length() / DEPTH_CHUNK_SIZE;
  }

  public int getDepth() {
    return CommentPath.calDepth(path);
  }

  public boolean isRoot() {
    return CommentPath.calDepth(path) == 1;
  }

  public String getParentPath() {
    return path.substring(0, path.length() - DEPTH_CHUNK_SIZE);
  }

  public CommentPath createChildCommentPath(String descendantsTopPath) {
    if (descendantsTopPath == null) return CommentPath.create(path + MIN_CHUNK);

    final var childrenTopPath = this.findChildrenTopPath(descendantsTopPath);
    return CommentPath.create(increase(childrenTopPath));
  }

  private String findChildrenTopPath(String descendantsTopPath) {
    return descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE);
  }

  private String increase(String path) {
    final var lastChunk = path.substring(path.length() - DEPTH_CHUNK_SIZE);

    if (isChunkOverflowed(lastChunk)) throw new IllegalStateException("chunk overflowed");

    final var charsetLength = CHARSET.length();
    int value = 0;

    for (char ch : lastChunk.toCharArray()) {
      value = value * charsetLength + CHARSET.indexOf(ch);
    }

    value = value + 1;

    final var result = new StringBuilder();
    for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
      result.append(CHARSET.charAt(value % charsetLength));
      value /= charsetLength;
    }

    return path.substring(0, path.length() - DEPTH_CHUNK_SIZE) + result.reverse();
  }

  private boolean isChunkOverflowed(String lastChunk) {
    return MAX_CHUNK.equals(lastChunk);
  }

}
