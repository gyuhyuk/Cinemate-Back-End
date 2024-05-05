package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Movie.dto.MovieDto;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.type.MovieSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieDto> searchMovies(MovieSearchType title, String search_keyword) {
//        return movieRepository.findAll();
        return List.of();
    }


}
