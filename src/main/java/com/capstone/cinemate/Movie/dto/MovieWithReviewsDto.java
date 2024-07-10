package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Member.dto.MemberDto;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.dto.MovieReviewDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record MovieWithReviewsDto(Set<MovieReviewDto> movieReviewDtos,
                                  Long movieId, Double rating, String backdropPath, String originalTitle,
                                  String movieTitle, LocalDateTime releaseDate, String posterPath,
                                  String overview, Boolean isLiked) {
    public static MovieWithReviewsDto of(Set<MovieReviewDto> movieReviewDtos,
                                         Long movieId, Double rating, String backdropPath, String originalTitle,
                                         String movieTitle, LocalDateTime releaseDate, String posterPath,
                                         String overview, Boolean isLiked) {
        return new MovieWithReviewsDto(movieReviewDtos, movieId, rating, backdropPath, originalTitle, movieTitle, releaseDate, posterPath, overview, isLiked);
    }

    public static MovieWithReviewsDto from(Movie entity, Long memberId, Boolean isLiked) {
        Set<MovieReviewDto> reviewDtos = entity.getMovieReviews().stream()
                .map(review -> MovieReviewDto.from(review, review.getMember().getId().equals(memberId), isLiked))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new MovieWithReviewsDto(
                reviewDtos,
                entity.getMovieId(),
                entity.getRating(),
                entity.getBackdropPath(),
                entity.getOriginalTitle(),
                entity.getMovieTitle(),
                entity.getReleaseDate(),
                entity.getPosterPath(),
                entity.getOverview(),
                isLiked
        );
    }
}