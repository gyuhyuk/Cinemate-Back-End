package com.capstone.cinemate.Movie.repository;

import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByMovieTitle(String movieTitle);

    @Query("SELECT m FROM Movie m WHERE REPLACE(m.movieTitle, ' ', '') LIKE %:searchValue%")
    List<Movie> findByMovieTitleContaining(@Param("searchValue") String searchValue);

    List<MovieResponse> findAllByIdIn(List<Long> genreMovieIdList);
    List<Movie> findMoviesByIdIn(List<Long> genreMovieIdList);

//    @Query("SELECT m FROM Movie m WHERE " +
//            "LOWER(m.movieTitle) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
//            "OR LOWER(m.movieTitle) LIKE LOWER(CONCAT('%', REPLACE(:searchValue, ' ', ''), '%'))")
//    List<Movie> findByMovieTitleContainingOrIgnoreSpaces(@Param("searchValue") String searchValue);
}
