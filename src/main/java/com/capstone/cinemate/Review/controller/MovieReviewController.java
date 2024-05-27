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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MovieReviewController {
    private final MovieReviewService movieReviewService;

    // 리뷰 별점 등록 및 수정
    @PutMapping("/api/movie/{movieId}/review/rating")
    public CustomResponse<MovieReviewResponse> postNewRating(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewRatingRequest request, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.createOrUpdateMovieRating(request, movieId, memberId);
        // 추후에 바뀔 수 있음
//        if (movieReviewDto == null) {
//            // 리뷰가 삭제된 경우
//            return new CustomResponse<>(HttpStatus.NO_CONTENT.value(), "별점이 삭제되었습니다.", null);
//        }
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.CREATED.value(), "별점이 등록되었습니다.", reviewResponse);
    }

    @GetMapping("/api/movie/{movieId}/review/rating")
    public CustomResponse<?> getRating(@PathVariable("movieId") Long movieId, @TokenInformation Long memberId) {
        Double rating = movieReviewService.getRating(movieId, memberId);
        return new CustomResponse<>(HttpStatus.OK.value(), "success", rating);
    }

    // 나의 리뷰 내용 조회
    @GetMapping("/api/myreview")
    public CustomResponse<?> searchMyReview(@TokenInformation Long memberId) {
        List<MovieReviewDto> reviews = movieReviewService.searchMyReview(memberId);

        List<MovieReviewDto> filteredReviews = reviews.stream()
                .filter(review -> review.content() != null && !review.content().isEmpty())
                .collect(Collectors.toList());

        return new CustomResponse<>(HttpStatus.OK.value(), "success", filteredReviews);
    }

    // 각 영화들에 대한 리뷰 조회
    @GetMapping("/api/movie/{movieId}/review/content")
    public CustomResponse<List<MovieReviewDto>> searchMovieReview(@RequestParam(required = false, defaultValue = "createdAt", value = "orderby") String criteria, @TokenInformation Long memberId, @PathVariable("movieId") Long movieId) {
        List<MovieReviewDto> movieReviews = movieReviewService.searchMovieReview(movieId, memberId, criteria);

        return new CustomResponse<>(HttpStatus.OK.value(), "success", movieReviews);
    }

    // 리뷰 내용 등록
    @PostMapping("/api/movie/{movieId}/review/content")
    public CustomResponse<MovieReviewResponse> postNewReview(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewContentRequest request, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.saveMovieContent(request, movieId, memberId);
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 등록되었습니다.", reviewResponse);
    }


    // 리뷰 내용 수정
    @PutMapping("/api/movie/{movieId}/review/content")
    public CustomResponse<MovieReviewResponse> updateReview(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewContentRequest request, @TokenInformation Long memberId) {
        MovieReviewDto movieReviewDto = movieReviewService.updateMovieContent(request, movieId, memberId);
        MovieReviewResponse reviewResponse = MovieReviewResponse.from(movieReviewDto);
        return new CustomResponse<>(HttpStatus.OK.value(), "리뷰가 수정되었습니다.", reviewResponse);
    }


    // 리뷰 삭제
    @DeleteMapping("/api/movie/{movieId}/review")
    public CustomResponse<Long> deleteMovieReview(@PathVariable Long movieId, @TokenInformation Long memberId) {
        movieReviewService.deleteMovieReview(movieId, memberId);

        return new CustomResponse<>(HttpStatus.OK.value(), "리뷰가 삭제되었습니다.", null);
    }
}
