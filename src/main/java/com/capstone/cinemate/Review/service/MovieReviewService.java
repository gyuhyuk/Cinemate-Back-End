package com.capstone.cinemate.Review.service;

import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class MovieReviewService {

    private final MovieRepository movieRepository;
    private final MovieReviewRepository movieReviewRepository;

    // 리뷰 등록
    public void saveMovieReview(MovieReviewDto dto) {

    }

    //
    public void updateMovieReview(MovieReviewDto dto) {

    }

    // 리뷰 삭제
    public void deleteMovieReview(long movieId) {

    }


}
