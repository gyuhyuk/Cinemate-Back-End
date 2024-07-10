package com.capstone.cinemate.Movie.repository;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

public interface MemberMovieRepository extends JpaRepository<MemberMovie, Long> {

    @Query("SELECT mo FROM MemberMovie m INNER JOIN Movie mo ON m.movie.id = mo.id WHERE m.member.id = :memberId")
    List<Movie> findMemberMoviesByMemberId(Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MemberMovie m WHERE m.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    List<MemberMovie> findByMemberId(Long memberId);
}
