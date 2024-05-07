//package com.capstone.cinemate.Movie.service;
//
//import com.capstone.cinemate.Movie.dto.MovieDto;
//import com.capstone.cinemate.Movie.dto.MovieWithReviewsDto;
//import com.capstone.cinemate.Movie.repository.MovieRepository;
//import com.capstone.cinemate.common.type.MovieSearchType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional
//@Service
//@RequiredArgsConstructor
//public class MovieService {
//    private final MovieRepository movieRepository;
//
//    @Transactional(readOnly = true)
//    public Page<MovieDto> searchMovies(MovieSearchType title, String searchKeyword, Pageable pageable) {
//        if(searchKeyword == null || searchKeyword.isBlank()) {
//            return movieRepository.findAll(pageable).map(MovieDto::from);
//        }
//
//        if(title == MovieSearchType.TITLE) {
//           return movieRepository.findByMovieTitle(searchKeyword, pageable).map(MovieDto::from);
//        }
//        return movieRepository.findAll(pageable).map(MovieDto::from);
//    }
//
//    @Transactional(readOnly = true)
//    public MovieWithReviewsDto getMovie(Long movieId) {
//        return movieRepository.findById(movieId)
//                .map()
//    }
//}
