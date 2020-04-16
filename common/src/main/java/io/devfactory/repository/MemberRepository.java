package io.devfactory.repository;

import io.devfactory.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findMemberByEmail(String email);

}
