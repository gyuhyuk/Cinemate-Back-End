package com.capstone.cinemate.Movie.Controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Movie.dto.MovieDto;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.Movie.service.MovieService;
import com.capstone.cinemate.common.response.CustomResponse;
import com.capstone.cinemate.common.type.MovieSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/api/search-movies")
    public ResponseEntity<CustomResponse<List<MovieDto>>> movies(
            @RequestParam(required = false) MovieSearchType movieSearchType,
            @RequestParam(required = false) String searchValue) {
        List<MovieDto> movies;


        if(searchValue != null && !searchValue.isBlank()) {
            movies = movieService.searchMoviesByPartialTitle(movieSearchType, searchValue);
        }
        else {
            movies = movieService.getAllMovies();
        }
        CustomResponse<List<MovieDto>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", movies);
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("/api/detail/{movieId}")
//    public ResponseEntity<CustomResponse<List<MovieDto>>> movieDetails(
//            @PathVariable Long movieId) {
//        MovieWithReviewResponse movie = MovieWithReviewResponse.from(movieService.)
//    }

    // 멤버-영화 조회
    @GetMapping("/api/member-movies")
    public ResponseEntity<CustomResponse<MoviesResponse>> getMemberMovies(@TokenInformation Long memberId) {
        MoviesResponse response = movieService.getMemberMovies(memberId);
        CustomResponse<MoviesResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }

    // 멤버-영화 저장
    @PostMapping("/api/member-movies")
    public ResponseEntity<CustomResponse<List<Long>>> saveMemberMovies(@RequestParam List<Long> movieIds, @TokenInformation Long memberId) {
        movieService.saveMemberMovie(movieIds, memberId);
        CustomResponse<List<Long>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", movieIds);
        return ResponseEntity.ok().body(response);
    }
}
