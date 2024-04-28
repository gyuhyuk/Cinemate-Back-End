package com.capstone.cinemate.Review.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.capstone.cinemate.Review.domain.Review}
 */
public record ReviewDto(String content, Double rating,
                        Long likes, LocalDateTime createdAt,
                        String createdBy, LocalDateTime modifiedAt,
                        String modifiedBy) implements Serializable {
    public static ReviewDto of(String content, Double rating, Long likes, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ReviewDto(content, rating, likes, createdAt, createdBy, modifiedAt, modifiedBy);
    }
}