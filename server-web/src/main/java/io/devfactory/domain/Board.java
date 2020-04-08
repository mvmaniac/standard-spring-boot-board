package io.devfactory.domain;

import io.devfactory.domain.base.BaseEntity;
import io.devfactory.domain.enums.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "tb_board")
@Entity
public class Board extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "board_idx")
  private Long idx;

  @Column(name = "board_title", length = 50, nullable = false)
  private String title;

  @Column(name = "board_sub_title", length = 100)
  private String subTitle;

  @Column(name = "board_content", length = 4000)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BoardType boardType;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "member_idx", foreignKey = @ForeignKey(name = "fk_tb_board_tb_member_01"))
  private Member member;

  @Builder(builderMethodName = "create")
  protected Board(String title, String subTitle, String content, BoardType boardType, Member member) {
    this.title = title;
    this.subTitle = subTitle;
    this.content = content;
    this.boardType = boardType;
    this.member = member;
  }

}
