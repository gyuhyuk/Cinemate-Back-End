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
                             Long likes, MemberReviewDto memberDto, LocalDateTime createdAt,
                             LocalDateTime modifiedAt) {
    public static MovieReviewDto of(Long id, Long movieId, String content, Double rating, Long likes, MemberReviewDto memberDto, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new MovieReviewDto(id, movieId, content, rating, likes, memberDto, createdAt, modifiedAt);
    }

    public static MovieReviewDto of(Long movieId, String content, Double rating, MemberReviewDto memberDto) {
        return new MovieReviewDto(null, movieId, content, rating, null, memberDto, null, null);
    }

    public static MovieReviewDto of(Long movieId, Double rating, MemberReviewDto memberDto) {
        return new MovieReviewDto(null, movieId, null, rating, null, memberDto, null, null);
    }

    public static MovieReviewDto from(Review entity) {
        return new MovieReviewDto(
                entity.getId(),
                entity.getMovie().getId(),
                entity.getContent(),
                entity.getRating(),
                entity.getLikes(),
                MemberReviewDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}