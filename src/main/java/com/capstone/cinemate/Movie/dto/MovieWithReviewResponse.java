package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Review.dto.MovieReviewResponse;

import java.time.LocalDateTime;
import java.util.Set;

public record MovieWithReviewResponse(Long id, Long movieId,
                                      Double rating, String backdropPath,
                                      String originalTitle, LocalDateTime releaseDate,
                                      String posterPath, String overview,
                                      Set<MovieReviewResponse> movieReviewResponses) {
    public static MovieWithReviewResponse of(Long id, Long movieId, Double rating, String backdropPath, String originalTitle, LocalDateTime releaseDate, String posterPath, String overview, Set<MovieReviewResponse> movieReviewResponses) {
        return new MovieWithReviewResponse(id, movieId, rating, backdropPath, originalTitle, releaseDate, posterPath, overview, movieReviewResponses);
    }

    public static MovieWithReviewResponse from(MovieWithReviewsDto dto) {
        String nickname = dto.memberDto().nickName();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.memberDto().memberId();
        }

        return new MovieWithReviewResponse(
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
