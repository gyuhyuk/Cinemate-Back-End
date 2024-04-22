package com.capstone.cinemate.Member.repository;

import com.capstone.cinemate.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
//        Optional<Member> findByMemberIdAndPassword(String memberId, String password);
        Optional<Member> findByMemberId(String memberId);
}
