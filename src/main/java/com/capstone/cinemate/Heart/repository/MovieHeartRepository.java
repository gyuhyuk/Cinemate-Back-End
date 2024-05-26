package com.capstone.cinemate.Heart.repository;

import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieHeartRepository extends JpaRepository<MovieHeart, Long> {
    Optional<MovieHeart> findByMemberAndMovie(Member member, Movie movie);

    @Query("SELECT mo FROM MovieHeart m INNER JOIN Movie mo ON m.movie.id = mo.id WHERE m.member.id = :memberId")
    List<Movie> findLikeMoviesByMemberId(Long memberId);
}
