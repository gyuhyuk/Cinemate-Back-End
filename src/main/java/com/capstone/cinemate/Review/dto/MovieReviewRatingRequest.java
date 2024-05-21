package com.capstone.cinemate.Review.dto;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.domain.Review;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 영화 별점 요청
@Data
public class MovieReviewRatingRequest {
    @NotBlank(message="별점은 필수 입력값입니다.")
    private Double rating;

    public Review toEntity(Member member, Movie movie){
        return Review.builder()
                .rating(this.rating)
                .member(member)
                .movie(movie)
                .build();
    }
}