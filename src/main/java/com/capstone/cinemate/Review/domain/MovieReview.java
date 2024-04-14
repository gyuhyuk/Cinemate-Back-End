package com.capstone.cinemate.Review.domain;

import com.capstone.cinemate.Movie.domain.Movie;

import java.time.LocalDateTime;

public class MovieReview {
    private Long id; // PK
    private Movie movieId; // 영화 ID
    private String content; // 리뷰 내용
    private double rating; // 평점
    private Long likes; // 좋아요 수

    // 메타 데이터
    private LocalDateTime createdAt; // 작성일시
    private String createdBy; // 작성자
    private LocalDateTime modifiedAt; // 수정일시
    private String modifiedBy; // 수정자

}
