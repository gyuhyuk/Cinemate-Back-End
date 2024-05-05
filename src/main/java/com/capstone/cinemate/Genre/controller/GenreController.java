package com.capstone.cinemate.Genre.controller;

import com.capstone.cinemate.Genre.service.GenreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

//    @GetMapping("/api/survey")

}
