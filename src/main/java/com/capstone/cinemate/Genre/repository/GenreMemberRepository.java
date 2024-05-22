package com.capstone.cinemate.Genre.repository;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreMemberRepository extends JpaRepository<GenreMember, Long> {

    @Query("SELECT ge FROM GenreMember g INNER JOIN Genre ge ON g.genre.id = ge.id WHERE g.member.id = :memberId")
    List<Genre> findGenreMembersByMemberId(Long memberId);

    @Query("SELECT g.genre.id FROM GenreMember g WHERE g.member.id = :memberId")
    List<Long> findGenreIdsByMemberId(Long memberId);
}
