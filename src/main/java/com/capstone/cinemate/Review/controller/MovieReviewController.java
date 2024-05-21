package com.capstone.cinemate.Review.controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.dto.MovieReviewContentRequest;
import com.capstone.cinemate.Review.dto.MovieReviewRatingRequest;
import com.capstone.cinemate.Review.dto.MovieReviewResponse;
import com.capstone.cinemate.Review.service.MovieReviewService;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MovieReviewController {
    private final MovieReviewService movieReviewService;

    // 리뷰 별점 등록 및 수정
    @PutMapping("/api/movie/{movieId}/review/{reviewId}/rating")
    public CustomResponse<MovieReviewResponse> postNewRating(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewRatingRequest request, @PathVariable("reviewId") Long reviewId, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.createOrUpdateMovieRating(request, movieId, reviewId, memberId);
        if (movieReviewDto == null) {
            // 리뷰가 삭제된 경우
            return new CustomResponse<>(HttpStatus.NO_CONTENT.value(), "별점이 삭제되었습니다.", null);
        }
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.CREATED.value(), "별점이 등록되었습니다.", reviewResponse);
    }


    // 리뷰 내용 등록
    @PostMapping("/api/movie/{movieId}/review/content")
    public CustomResponse<MovieReviewResponse> postNewReview(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewContentRequest request, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.saveMovieContent(request, movieId, memberId);
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 등록되었습니다.", reviewResponse);
    }


    // 리뷰 내용 수정
    @PutMapping("/api/movie/{movieId}/review/content/{reviewId}")
    public CustomResponse<MovieReviewResponse> updateReview(@PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId, @RequestBody MovieReviewContentRequest request, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.updateMovieContent(request, movieId, reviewId, memberId);
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 수정되었습니다.", reviewResponse);
    }


//    @DeleteMapping("/api/movie/{movieId}/review/{reviewId}")
//    public CustomResponse<Long> deleteMovieReview(@PathVariable Long movieId, @PathVariable Long reviewId, @TokenInformation Long memberId) {
//        movieReviewService.deleteMovieReview(reviewId, member.getMemberId());
//
//        return new CustomResponse<>(HttpStatus.OK.value(), "리뷰가 삭제 되었습니다.", reviewId);
//    }
}
