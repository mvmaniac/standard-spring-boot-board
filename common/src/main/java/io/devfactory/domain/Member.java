package io.devfactory.domain;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import io.devfactory.domain.base.BaseEntity;
import io.devfactory.domain.enums.SocialType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(
  name = "tb_member",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_tb_member_01", columnNames = {"member_email"})
  }
)
@Entity
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "member_idx")
  private Long idx;

  @Column(name = "member_name", length = 10, nullable = false)
  private String name;

  @Column(name = "member_password", length = 20)
  private String password;

  @Column(name = "member_email", length = 30)
  private String email;

  @Column(name = "member_principal", length = 100)
  private String principal;

  @Column(name = "member_social_type", length = 10)
  @Enumerated(STRING)
  private SocialType socialType;

  @Builder(builderMethodName = "create")
  public Member(String name, String password, String email, String principal,
    SocialType socialType) {
    this.name = name;
    this.password = password;
    this.email = email;
    this.principal = principal;
    this.socialType = socialType;
  }

}
