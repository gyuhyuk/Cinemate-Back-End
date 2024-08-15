package com.capstone.cinemate.Hate.controller;

import com.capstone.cinemate.Hate.service.MovieHateService;
import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieHateController {
    private final MovieHateService movieHateService;

    // 영화 싫어요 누르기
    @PostMapping("/api/movie/hates/{movieId}")
    public CustomResponse<?> handleDislikes(@PathVariable("movieId") Long movieId, @TokenInformation Long memberId) {
        boolean isDisliked = movieHateService.insertDislikes(movieId, memberId);
        String message = isDisliked ? "관심없음을 눌렀습니다." : "관심없음을 취소했습니다.";
        return new CustomResponse<>(HttpStatus.OK.value(), "Success", message);
    }

    // 싫어요 누른 영화 조회
    @GetMapping("/api/movie/hates")
    public ResponseEntity<CustomResponse<MoviesResponse>> showDisLikeMovies(@TokenInformation Long memberId) {
        MoviesResponse response = movieHateService.getDislikeMovies(memberId);
        CustomResponse<MoviesResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }
}
