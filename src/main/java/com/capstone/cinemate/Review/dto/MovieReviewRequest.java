package com.capstone.cinemate.Review.dto;

import com.capstone.cinemate.Member.dto.MemberDto;

// 영화 리뷰 요청
public record MovieReviewRequest(Long movieId, String content, Double rating) {

    public static MovieReviewRequest of(Long movieId, String content, Double rating) {
        return new MovieReviewRequest(movieId, content, rating);
    }

    public MovieReviewDto toDto(MemberDto memberDto) {
        return MovieReviewDto.of(
                movieId,
                content,
                rating,
                memberDto
        );
    }
}
