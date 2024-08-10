package com.capstone.cinemate.Hate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieHateController {
    private final MovieHateService movieHateService;

}
