package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Movie.domain.Movie;

import java.time.LocalDateTime;

public record MovieWithoutMovieIdResponse(Long movieId, Double rating, String backdropPath,
                            String originalTitle, String movieTitle, LocalDateTime releaseDate,
                            String posterPath,
                            String overview
) {

    public static MovieWithoutMovieIdResponse of(Movie movie) {
        return new MovieWithoutMovieIdResponse(
                movie.getId(),
                movie.getRating(),
                movie.getBackdropPath(),
                movie.getOriginalTitle(),
                movie.getMovieTitle(),
                movie.getReleaseDate(),
                movie.getPosterPath(),
                movie.getOverview());
    }
}
