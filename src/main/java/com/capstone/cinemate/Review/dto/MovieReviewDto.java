package com.capstone.cinemate.Review.dto;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.MemberDto;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.domain.Review;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.capstone.cinemate.Review.domain.Review}
 */
public record MovieReviewDto(Long id, Long movieId, String content, Double rating,
                             Long likes, MemberDto memberDto, LocalDateTime createdAt,
                             String createdBy, LocalDateTime modifiedAt,
                             String modifiedBy) {
    public static MovieReviewDto of(Long id, Long movieId, String content, Double rating, Long likes, MemberDto memberDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new MovieReviewDto(id, movieId, content, rating, likes, memberDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static MovieReviewDto from(Review entity) {
        return new MovieReviewDto(
                entity.getId(),
                entity.getMovie().getId(),
                entity.getContent(),
                entity.getRating(),
                entity.getLikes(),
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Review toEntity(Movie entity) {
        return Review.of(
                entity,
                content,
                rating
        );
    }
}