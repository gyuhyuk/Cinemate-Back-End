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
                       String overview,
                       Boolean isLiked,
                       Boolean isDisliked
) {
    public static MovieDto of(Long id, Long movieId, Double rating, String backdropPath, String originalTitle, String movieTitle, LocalDateTime releaseDate, String posterPath, String overview, Boolean isLiked, Boolean isDisliked) {
        return new MovieDto(id, movieId, rating, backdropPath, originalTitle, movieTitle, releaseDate, posterPath, overview, isLiked, isDisliked);
    }

    public static MovieDto from(Movie entity, Boolean isLiked, Boolean isDisliked) {
        return new MovieDto(
                entity.getId(),
                entity.getMovieId(),
                entity.getRating(),
                entity.getBackdropPath(),
                entity.getOriginalTitle(),
                entity.getMovieTitle(),
                entity.getReleaseDate(),
                entity.getPosterPath(),
                entity.getOverview(),
                isLiked,
                isDisliked
        );
    }
}