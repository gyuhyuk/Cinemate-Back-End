package com.capstone.cinemate.Genre.dto;

import com.capstone.cinemate.Genre.domain.Genre;

public record GenreResponse(Long id, String genreName) {

    public static GenreResponse of(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getGenreName()
        );
    }
}
