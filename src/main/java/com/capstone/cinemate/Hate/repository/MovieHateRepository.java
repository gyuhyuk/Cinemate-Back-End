package com.capstone.cinemate.Hate.repository;

import com.capstone.cinemate.Hate.domain.MovieHate;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieHateRepository extends JpaRepository<MovieHate, Long> {
    Optional<MovieHate> findByMemberAndMovie(Member member, Movie movie);
    List<MovieHate> findByMemberId(Long memberId);
    @Query("SELECT mo FROM MovieHate m INNER JOIN Movie mo ON m.movie.id = mo.id WHERE m.member.id = :memberId")
    List<Movie> findHateMoviesByMemberId(Long memberId);

    boolean existsByMemberIdAndMovieId(Long memberId, Long movieId);
}
