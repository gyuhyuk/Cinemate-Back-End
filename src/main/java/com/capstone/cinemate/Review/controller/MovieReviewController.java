package com.capstone.cinemate.Review.controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Review.dto.MovieReviewRequest;
import com.capstone.cinemate.Review.service.MovieReviewService;
import com.capstone.cinemate.common.response.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
@Slf4j
public class MovieReviewController {
    private final MovieReviewService movieReviewService;

    @PostMapping("/api/movie/{movieId}/review")
    public CustomResponse<Long> postNewReview(@RequestBody MovieReviewRequest request, @TokenInformation Long memberId) {
        log.info("member id : " + memberId);
//        movieReviewService.saveMovieReview(request);

        return new CustomResponse<>(HttpStatus.CREATED.value(), "리뷰가 등록되었습니다.", request.movieId());
    }

    @DeleteMapping("/api/movie/{movieId}/review/{reviewId}")
    public CustomResponse<Long> deleteMovieReview(@PathVariable Long movieId, @PathVariable Long reviewId, @Valid Member member) {
        movieReviewService.deleteMovieReview(reviewId, member.getMemberId());

        return new CustomResponse<>(HttpStatus.OK.value(), "리뷰가 삭제 되었습니다.", reviewId);
    }
}
