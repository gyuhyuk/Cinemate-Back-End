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

    public static MovieWithReviewsDto from(Movie entity, Boolean isLiked) {
        return new MovieWithReviewsDto(
                entity.getMovieReviews().stream().map(MovieReviewDto::from)
                                .collect(Collectors.toCollection(LinkedHashSet::new)),
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
