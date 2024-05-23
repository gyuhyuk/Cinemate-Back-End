package com.capstone.cinemate.Movie.repository;

import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByMovieTitle(String movieTitle);
    List<Movie> findByMovieTitleContaining(String searchValue);

    List<MovieResponse> findAllByIdIn(List<Long> genreMovieIdList);
    List<Movie> findMoviesByIdIn(List<Long> genreMovieIdList);
}
