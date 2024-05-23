package com.capstone.cinemate.Review.dto;

import java.time.LocalDateTime;

// 영화 리뷰 응답
public record MovieReviewResponse(Long id, Long movieId, String content, Double rating, Long likes, String nickname, LocalDateTime createdAt) {
    public static MovieReviewResponse of(Long id, Long movieId, String content, Double rating, Long likes, String nickname, LocalDateTime createdAt) {
        return new MovieReviewResponse(id, movieId, content, rating, likes, nickname, createdAt);
    }

    public static MovieReviewResponse from(MovieReviewDto dto) {
        String nickname = dto.memberDto().nickName();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.memberDto().email();
        }

        return new MovieReviewResponse(
                dto.id(),
                dto.movieId(),
                dto.content(),
                dto.rating(),
                dto.likes(),
                nickname,
                dto.createdAt()
        );
    }
}
