package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Movie.domain.Movie;

import java.time.LocalDateTime;

public record MovieResponse(Long id, Long movieId, Double rating, String backdropPath,
                            String originalTitle, String movieTitle, LocalDateTime releaseDate,
                            String posterPath,
                            String overview
) {

    public static MovieResponse of(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getMovieId(),
                movie.getRating(),
                movie.getBackdropPath(),
                movie.getOriginalTitle(),
                movie.getMovieTitle(),
                movie.getReleaseDate(),
                movie.getPosterPath(),
                movie.getOverview());
    }
}
