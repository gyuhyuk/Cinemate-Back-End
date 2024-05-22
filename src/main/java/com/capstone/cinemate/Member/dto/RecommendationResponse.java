package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Movie.domain.Movie;

import java.util.List;

public class RecommendationResponse {
    private List<Movie> defaultRecommendResult;
    private List<List<Movie>> genreMovieLists;

    public List<Movie> getDefaultRecommendResult() {
        return defaultRecommendResult;
    }

    public void setDefaultRecommendResult(List<Movie> defaultRecommendResult) {
        this.defaultRecommendResult = defaultRecommendResult;
    }

    public List<List<Movie>> getGenreMovieLists() {
        return genreMovieLists;
    }

    public void setGenreMovieLists(List<List<Movie>> genreMovieLists) {
        this.genreMovieLists = genreMovieLists;
    }
}