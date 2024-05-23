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

    public static MovieWithReviewsResponse from(MovieWithReviewsDto dto) {
        String nickname = dto.memberDto().nickName();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.memberDto().email();
        }

        return new MovieWithReviewsResponse(
                dto.id(),
                dto.movieId(),
                dto.rating(),
                dto.backdropPath(),
                dto.originalTitle(),
                dto.releaseDate(),
                dto.posterPath(),
                dto.overview(),
                null
        );
    }
}
