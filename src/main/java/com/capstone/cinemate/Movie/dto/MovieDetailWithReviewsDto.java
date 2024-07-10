package com.capstone.cinemate.Movie.dto;

import com.capstone.cinemate.Review.dto.MovieReviewDto;

import java.util.List;

public record MovieDetailWithReviewsDto(MovieDetailDto movieDetail, List<MovieReviewDto> movieReviewDto) {
}
