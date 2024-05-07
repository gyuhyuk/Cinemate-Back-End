package com.capstone.cinemate.Movie.repository;

import com.capstone.cinemate.Movie.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByMovieTitle(String movieTitle, Pageable pageable);
}