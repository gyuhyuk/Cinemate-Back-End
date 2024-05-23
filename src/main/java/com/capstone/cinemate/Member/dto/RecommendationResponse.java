package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Movie.domain.Movie;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecommendationResponse {
    private List<Movie> defaultRecommendResult;
    private List<List<Movie>> genreMovieLists;
}