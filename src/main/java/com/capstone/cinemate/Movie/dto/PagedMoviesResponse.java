package com.capstone.cinemate.Movie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagedMoviesResponse {
    private MoviesResponse moviesResponse;
    private int totalPages;
    private long totalElements;

    public PagedMoviesResponse(MoviesResponse moviesResponse, int totalPages, long totalElements) {
        this.moviesResponse = moviesResponse;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}