package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Movie.dto.MovieListWithGenreId;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendationResponse {
    private List<MovieResponse> defaultRecommendResult;
    private List<MovieListWithGenreId> genreMovieLists;
}