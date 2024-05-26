package com.capstone.cinemate.Movie.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MovieListWithGenreId {
    Long genreId;
    List<MovieResponse> movieList;
}
