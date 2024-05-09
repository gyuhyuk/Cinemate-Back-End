package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Movie.dto.MovieDto;
import com.capstone.cinemate.Movie.dto.MovieWithReviewsDto;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.type.MovieSearchType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    // 영화 검색
    @Transactional(readOnly = true)
    public List<MovieDto> searchMovies(MovieSearchType title, String searchKeyword) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return movieRepository.findAll().stream()
                    .map(MovieDto::from)
                    .collect(Collectors.toList());
        }

        // 영화 제목이 일치할 때
        if (title == MovieSearchType.TITLE) {
            return movieRepository.findByMovieTitle(searchKeyword).stream()
                    .map(MovieDto::from)
                    .collect(Collectors.toList());
        }
        return movieRepository.findAll().stream()
                .map(MovieDto::from)
                .collect(Collectors.toList());
    }

    // 영화 정보 입력 시 리뷰 까지 같이 조회
    @Transactional(readOnly = true)
    public MovieWithReviewsDto getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .map(MovieWithReviewsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("영화가 없습니다. - movieId: " + movieId));
    }
}
