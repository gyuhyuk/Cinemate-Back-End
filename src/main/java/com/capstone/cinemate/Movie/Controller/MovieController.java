package com.capstone.cinemate.Movie.Controller;

import com.capstone.cinemate.Genre.dto.GenreDto;
import com.capstone.cinemate.Movie.dto.MovieDto;
import com.capstone.cinemate.Movie.service.MovieService;
import com.capstone.cinemate.common.response.CustomResponse;
import com.capstone.cinemate.common.type.MovieSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/api/movies")
    public ResponseEntity<CustomResponse<List<MovieDto>>> movies(
            @RequestParam(required = false) MovieSearchType movieSearchType,
            @RequestParam(required = false) String searchValue) {
        List<MovieDto> movies = movieService.searchMovies(movieSearchType, searchValue);
        CustomResponse<List<MovieDto>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", movies);
        return ResponseEntity.ok().body(response);
    }


}
