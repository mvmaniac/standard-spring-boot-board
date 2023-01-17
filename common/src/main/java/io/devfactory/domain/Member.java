package io.devfactory.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import io.devfactory.domain.base.BaseEntity;
import io.devfactory.domain.enums.Grade;
import io.devfactory.domain.enums.SocialType;
import io.devfactory.domain.enums.Status;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.domain.Persistable;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(
    name = "tb_member",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tb_member_01", columnNames = {"member_email"})
    }
)
@Entity
public class Member extends BaseEntity implements Persistable<Long> {

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

  @Column(name = "member_grade", length = 10)
  @Enumerated(STRING)
  private Grade grade;

  @Column(name = "member_status", length = 10)
  @Enumerated(STRING)
  private Status status;

  @Builder(builderMethodName = "create")
  public Member(String name, String password, String email, String principal,
      SocialType socialType) {
    this.name = name;
    this.password = password;
    this.email = email;
    this.principal = principal;
    this.socialType = socialType;
  }

  public Member setInactive() {
    status = Status.INACTIVE;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Member member = (Member) o;

    return new EqualsBuilder()
        .append(idx, member.idx)
        .append(email, member.email)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(idx)
        .append(email)
        .toHashCode();
  }

  @Override
  public Long getId() {
    System.out.println("[dev] getId");
    return idx;
  }

  @Override
  public boolean isNew() {
    System.out.println("[dev] isNew: " + Objects.isNull(getCreatedDate()));
    return Objects.isNull(getCreatedDate());
  }

}
