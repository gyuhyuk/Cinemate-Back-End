package com.capstone.cinemate.Movie.domain;

import java.time.LocalDateTime;

public class Movie {
    private Long id; // PK
    private Long movieId; // 영화 ID
    private double rating; // 영화 평점
    private String backdropPath; // 배경 사진
    private String originalTitle; // 영화 원제
    private String movieTitle; // 영화 제목
    private LocalDateTime releaseDate; // 출시일
    private String posterPath; // 포스터 이미지
    private String overview; // 줄거리
}
