package com.capstone.cinemate.Review.dto;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.domain.Review;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 영화 리뷰 요청
@Data
public class MovieReviewContentRequest {
    @NotBlank(message="내용은 필수 입력값입니다.")
    private String content;

    public Review toEntity(Member member, Movie movie, Review review){
        return Review.builder()
                .content(this.content)
                .rating(review.getRating())
                .member(member)
                .movie(movie)
                .build();
    }
}