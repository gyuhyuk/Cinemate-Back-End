package com.capstone.cinemate.Hate.controller;

import com.capstone.cinemate.Hate.service.MovieHateService;
import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieHateController {
    private final MovieHateService movieHateService;

    // 싫어요 누른 영화 조회
    @GetMapping("/api/movie/hates")
    public ResponseEntity<CustomResponse<MoviesResponse>> showDisLikeMovies(@TokenInformation Long memberId) {
        MoviesResponse response = movieHateService.getDislikeMovies(memberId);
        CustomResponse<MoviesResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }
}
