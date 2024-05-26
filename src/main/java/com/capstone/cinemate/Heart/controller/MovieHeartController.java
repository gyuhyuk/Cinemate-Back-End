package com.capstone.cinemate.Heart.controller;

import com.capstone.cinemate.Heart.service.MovieHeartService;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieHeartController {
    private final MovieHeartService movieHeartService;

//    @PostMapping("/api/movie/likes/{movieId}")
//    public CustomResponse<?> handleLikes(@PathVariable("movieId") Long movieId) {
//
//    }
}
