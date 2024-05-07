package com.capstone.cinemate.Review.dto;

import java.time.LocalDateTime;

public record MovieReviewResponse(Long id, String content, Double rating, Long likes, String nickname, LocalDateTime createdAt) {
    public static MovieReviewResponse of(Long id,String content, Double rating, Long likes, String nickname, LocalDateTime createdAt) {
        return new MovieReviewResponse(id, content, rating, likes, nickname, createdAt);
    }

    public static MovieReviewResponse from(MovieReviewDto dto) {
        String nickname = dto.memberDto().nickName();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.memberDto().memberId();
        }

        return new MovieReviewResponse(
                dto.id(),
                dto.content(),
                dto.rating(),
                dto.likes(),
                nickname,
                dto.createdAt()
        );
    }
}
