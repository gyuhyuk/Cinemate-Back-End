package com.capstone.cinemate.Hate.controller;

import com.capstone.cinemate.Hate.service.MovieHateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieHateController {
    private final MovieHateService movieHateService;

}
