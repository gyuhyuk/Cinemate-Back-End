package com.capstone.cinemate.Review.dto;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.MemberDto;
import com.capstone.cinemate.Member.dto.MemberReviewDto;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.domain.Review;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.capstone.cinemate.Review.domain.Review}
 */
public record MovieReviewDto(Long id, Long movieId, String content, Double rating,
                             Long likes, MemberReviewDto member, LocalDateTime createdAt,
                             LocalDateTime modifiedAt, Boolean isMine) {
    public static MovieReviewDto of(Long id, Long movieId, String content, Double rating, Long likes, MemberReviewDto member, LocalDateTime createdAt, LocalDateTime modifiedAt, Boolean isMine) {
        return new MovieReviewDto(id, movieId, content, rating, likes, member, createdAt, modifiedAt, isMine);
    }

    public static MovieReviewDto of(Long movieId, String content, Double rating, MemberReviewDto member) {
        return new MovieReviewDto(null, movieId, content, rating, null, member, null, null, null);
    }

    public static MovieReviewDto of(Long movieId, Double rating, MemberReviewDto member) {
        return new MovieReviewDto(null, movieId, null, rating, null, member, null, null, null);
    }

    public static MovieReviewDto from(Review entity, Boolean isMine) {
        return new MovieReviewDto(
                entity.getId(),
                entity.getMovie().getId(),
                entity.getContent(),
                entity.getRating(),
                entity.getLikes(),
                MemberReviewDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                isMine
        );
    }
}