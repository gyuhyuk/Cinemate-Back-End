package com.capstone.cinemate.Movie.Controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Movie.dto.*;
import com.capstone.cinemate.Movie.service.MovieService;
import com.capstone.cinemate.common.response.CustomResponse;
import com.capstone.cinemate.common.type.MovieSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 전체 영화 조회
    @GetMapping("/api/search-movies")
    public ResponseEntity<CustomResponse<List<MovieDto>>> movies(
            @RequestParam(required = false) MovieSearchType movieSearchType,
            @RequestParam(required = false) String searchValue) {
        List<MovieDto> movies;

        if(searchValue != null && !searchValue.isBlank()) {
            movies = movieService.searchMoviesByPartialTitle(movieSearchType, searchValue);
        }
        else {
            movies = List.of();
        }
        if (movies.size() > 20) {
            movies = movies.subList(0, 20);
        }

        CustomResponse<List<MovieDto>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", movies);
        return ResponseEntity.ok().body(response);
    }

    // 멤버-영화 조회
    @GetMapping("/api/member-movies")
    public ResponseEntity<CustomResponse<MoviesResponse>> getMemberMovies(@TokenInformation Long memberId) {
        MoviesResponse response = movieService.getMemberMovies(memberId);

        CustomResponse<MoviesResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }

    // 멤버 - 설문결과 저장
    @PostMapping("/api/survey")
    public ResponseEntity<CustomResponse<Map<String, List<Long>>>> saveMemberGenreAndMemberMovie(@RequestParam List<Long> movieIds, @RequestParam List<Long> genreIds, @TokenInformation Long memberId) {
        movieService.saveMemberMovieSurveyResult(movieIds, genreIds, memberId);
        Map<String, List<Long>> result = new HashMap<>();
        result.put("movieIds", movieIds);
        result.put("genreIds", genreIds);

        CustomResponse<Map<String, List<Long>>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", result);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/movie/detail/{movieId}")
    public ResponseEntity<CustomResponse<MovieDetailDto>> getMovieDetail(@TokenInformation Long memberId, @PathVariable Long movieId) throws IOException, InterruptedException {
        MovieDetailDto response = movieService.getMovieDetails(movieId);

        CustomResponse<MovieDetailDto> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }

    @GetMapping("/api/movie/detail-review/{movieId}")
    public ResponseEntity<CustomResponse<MovieWithReviewsDto>> getMovieWithReviews(@PathVariable Long movieId) {
        MovieWithReviewsDto response = movieService.getMovieWithReviews(movieId);

        CustomResponse<MovieWithReviewsDto> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }
}