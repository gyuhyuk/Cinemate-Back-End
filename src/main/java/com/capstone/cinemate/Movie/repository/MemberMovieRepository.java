package com.capstone.cinemate.Movie.repository;

import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Movie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberMovieRepository extends JpaRepository<MemberMovie, Long> {

    @Query("SELECT mo FROM MemberMovie m INNER JOIN Movie mo ON m.movie.id = mo.id WHERE m.member.id = :memberId")
    List<Movie> findMemberMoviesByMemberId(Long memberId);

//    @Modifying
//    @Query(value = "insert into member_movie values (movie_id = :moviesId, member_id = :memberId)", nativeQuery = true)
//    void saveMovies(Long movieId, Long memberId);
}
