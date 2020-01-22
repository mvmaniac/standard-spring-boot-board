package io.devfactory.domain;

import io.devfactory.domain.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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

    @Column(name = "member_password", length = 20, nullable = false)
    private String password;

    @Column(name = "member_email", length = 30)
    private String email;

    @Builder(builderMethodName = "create")
    protected Member(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("idx", idx)
                .append("name", name)
                .append("password", password)
                .append("email", email)
                .toString();
    }

}
