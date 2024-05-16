package com.capstone.cinemate.Review.controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.dto.MovieReviewRequest;
import com.capstone.cinemate.Review.dto.MovieReviewResponse;
import com.capstone.cinemate.Review.service.MovieReviewService;
import com.capstone.cinemate.common.response.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MovieReviewController {
    private final MovieReviewService movieReviewService;

    @PostMapping("/api/movie/{movieId}/review")
    public CustomResponse<String> postNewReview(@PathVariable("movieId") Long movieId, @RequestBody MovieReviewRequest request, @TokenInformation Long memberId) {
        log.info("member id : " + memberId);
        movieReviewService.saveMovieReview(request, movieId, memberId);

        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 등록되었습니다.", request.content());
    }

    @PutMapping("api/movie/{movieId}/review/{reviewId}")
    public CustomResponse<String> updateReview(@PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId, @RequestBody MovieReviewRequest request, @TokenInformation Long memberId) {
        movieReviewService.updateMovieReview(request, movieId, reviewId, memberId);

        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 수정되었습니다.", request.content());
    }

//    @DeleteMapping("/api/movie/{movieId}/review/{reviewId}")
//    public CustomResponse<Long> deleteMovieReview(@PathVariable Long movieId, @PathVariable Long reviewId, @TokenInformation Long memberId) {
//        movieReviewService.deleteMovieReview(reviewId, member.getMemberId());
//
//        return new CustomResponse<>(HttpStatus.OK.value(), "리뷰가 삭제 되었습니다.", reviewId);
//    }
}
