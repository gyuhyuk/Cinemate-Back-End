package com.capstone.cinemate.Genre.repository;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreMemberRepository extends JpaRepository<GenreMember, Long> {

    @Query("SELECT g.genre.id FROM GenreMember g WHERE g.member.id = :memberId")
    List<Long> findGenreIdsByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM GenreMember g WHERE g.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    List<GenreMember> findByMemberId(Long memberId);
}
