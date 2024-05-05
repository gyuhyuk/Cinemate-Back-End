package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Movie.domain.Movie;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.capstone.cinemate.Movie.domain.Movie}
 */
public record MovieDto(Long id, Long movieId,
                       Double rating,
                       String backdropPath,
                       String originalTitle,
                       String movieTitle,
                       LocalDateTime releaseDate,
                       String posterPath,
                       String overview) {
    public static MovieDto of(Long id, Long movieId, Double rating, String backdropPath, String originalTitle, String movieTitle, LocalDateTime releaseDate, String posterPath, String overview) {
        return new MovieDto(id, movieId, rating, backdropPath, originalTitle, movieTitle, releaseDate, posterPath, overview);
    }

    public static MovieDto from(Movie entity) {
        return new MovieDto(
                entity.getId(),
                entity.getMovieId(),
                entity.getRating(),
                entity.getBackdropPath(),
                entity.getOriginalTitle(),
                entity.getMovieTitle(),
                entity.getReleaseDate(),
                entity.getPosterPath(),
                entity.getOverview()
        );
    }

    public Movie toEntity() {
        return Movie.of(
            movieId,
            rating,
            backdropPath,
            originalTitle,
            movieTitle,
            releaseDate,
            posterPath,
            overview
        );
    }
}