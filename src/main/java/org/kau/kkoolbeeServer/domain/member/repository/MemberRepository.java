package org.kau.kkoolbeeServer.domain.member.repository;

import org.kau.kkoolbeeServer.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
