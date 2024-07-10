package com.capstone.cinemate.Heart.controller;

import com.capstone.cinemate.Heart.service.MovieHeartService;
import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.Movie.dto.MoviesWithoutMovieIdResponse;
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
public class MovieHeartController {
    private final MovieHeartService movieHeartService;

    // 영화 좋아요 누르기
    @PostMapping("/api/movie/likes/{movieId}")
    public CustomResponse<?> handleLikes(@PathVariable("movieId") Long movieId, @TokenInformation Long memberId) {
        boolean isLiked = movieHeartService.insertLikes(movieId, memberId);
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        return new CustomResponse<>(HttpStatus.OK.value(), "Success", message);
    }

    // 좋아요 누른 영화 조회
    @GetMapping("/api/movie/likes")
    public ResponseEntity<CustomResponse<MoviesResponse>> showLikeMovies(@TokenInformation Long memberId) {
        MoviesResponse response = movieHeartService.getLikeMovies(memberId);
        CustomResponse<MoviesResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }
}
