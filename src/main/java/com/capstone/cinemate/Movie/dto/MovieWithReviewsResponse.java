package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Review.dto.MovieReviewResponse;

import java.time.LocalDateTime;
import java.util.Set;

public record MovieWithReviewsResponse(Long id, Long movieId,
                                       Double rating, String backdropPath,
                                       String originalTitle, LocalDateTime releaseDate,
                                       String posterPath, String overview,
                                       Set<MovieReviewResponse> movieReviewResponse) {
    public static MovieWithReviewsResponse of(Long id, Long movieId, Double rating, String backdropPath, String originalTitle, LocalDateTime releaseDate, String posterPath, String overview, Set<MovieReviewResponse> movieReviewResponses) {
        return new MovieWithReviewsResponse(id, movieId, rating, backdropPath, originalTitle, releaseDate, posterPath, overview, movieReviewResponses);
    }

}
