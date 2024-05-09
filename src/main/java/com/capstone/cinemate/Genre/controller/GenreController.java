package com.capstone.cinemate.Genre.controller;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.dto.GenreDto;
import com.capstone.cinemate.Genre.service.GenreService;
import com.capstone.cinemate.common.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/api/genres")
    public ResponseEntity<CustomResponse<List<GenreDto>>> getGenres() {
        List<GenreDto> genreDtos = genreService.getAllGenre();
        CustomResponse<List<GenreDto>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", genreDtos);

        return ResponseEntity.ok().body(response);
    }
}
