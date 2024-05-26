package com.capstone.cinemate.Heart.controller;

import com.capstone.cinemate.Heart.service.ReviewHeartService;
import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewHeartController {
    private final ReviewHeartService reviewHeartService;

    // 리뷰 좋아요
    @PostMapping("/api/review/likes/{movieId}/{reviewId}")
    public CustomResponse<?> handleLikes(@PathVariable("movieId") Long movieId, @PathVariable("reviewId") Long reviewId, @TokenInformation Long memberId) {
        boolean isLiked = reviewHeartService.insertLikes(movieId, reviewId, memberId);
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        return new CustomResponse<>(HttpStatus.OK.value(), "Success", message);
    }
}
