package io.devfactory.repository;

import io.devfactory.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import io.devfactory.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findMemberByEmail(String email);

  List<Member> findMembersByUpdatedDateBeforeAndStatusEquals(LocalDateTime localDateTime, Status status);

}
