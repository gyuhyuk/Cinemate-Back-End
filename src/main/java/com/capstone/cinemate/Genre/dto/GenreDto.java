package com.capstone.cinemate.Genre.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreDto {
    private Long id;
    private String genreName;

    public GenreDto(Long id, String genreName) {
        this.id = id;
        this.genreName = genreName;
    }

}
