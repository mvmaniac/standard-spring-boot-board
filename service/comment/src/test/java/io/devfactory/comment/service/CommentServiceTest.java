package io.devfactory.comment.service;

import io.devfactory.comment.entity.Comment;
import io.devfactory.comment.mapper.CommentMapper;
import io.devfactory.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @InjectMocks
  private CommentService commentService;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private CommentMapper commentMapper;

  @DisplayName("삭제할 댓글이 자식 있으면, 삭제 표시만 한다.")
  @Test
  void deleteShouldMarkDeletedIfHasChildren() {
    // given
    final var articleId = 1L;
    final var commentId = 2L;
    final var comment = createComment(articleId, commentId);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(commentMapper.countCommentChildren(articleId, commentId, 2L)).willReturn(2L);

    // when
    commentService.delete(commentId);

    // then
    verify(comment).delete();
  }

  @DisplayName("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
  @Test
  void deleteShouldDeleteChildOnlyIfNotDeletedParent() {
    // given
    final var articleId = 1L;
    final var commentId = 2L;
    final var parentCommentId = 1L;

    final var comment = createComment(articleId, commentId, parentCommentId);
    given(comment.isRoot()).willReturn(false);

    final var parentComment = mock(Comment.class);
    given(parentComment.getDeleted()).willReturn(false);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(commentMapper.countCommentChildren(articleId, commentId, 2L)).willReturn(1L);

    given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));

    // when
    commentService.delete(commentId);

    // then
    verify(commentRepository).delete(comment);
    verify(commentRepository, never()).delete(parentComment);
  }

  @DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다.")
  @Test
  void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
    // given
    final var articleId = 1L;
    final var commentId = 2L;
    final var parentCommentId = 1L;

    final var comment = this.createComment(articleId, commentId, parentCommentId);
    given(comment.isRoot()).willReturn(false);

    final var parentComment = this.createComment(articleId, parentCommentId);
    given(parentComment.isRoot()).willReturn(true);
    given(parentComment.getDeleted()).willReturn(true);

    given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
    given(commentMapper.countCommentChildren(articleId, commentId, 2L)).willReturn(1L);

    given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));
    given(commentMapper.countCommentChildren(articleId, parentCommentId, 2L)).willReturn(1L);

    // when
    commentService.delete(commentId);

    // then
    verify(commentRepository).delete(comment);
    verify(commentRepository).delete(parentComment);
  }

  private Comment createComment(Long articleId, Long commentId) {
    final var comment = mock(Comment.class);
    given(comment.getArticleId()).willReturn(articleId);
    given(comment.getCommentId()).willReturn(commentId);
    return comment;
  }

  private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
    final var comment = createComment(articleId, commentId);
    given(comment.getParentCommentId()).willReturn(parentCommentId);
    return comment;
  }

}
