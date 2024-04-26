package com.capstone.cinemate.Movie.domain;

import com.capstone.cinemate.Review.domain.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "movieTitle")
})
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    private Long movieId; // 영화 ID
    private Double rating; // 영화 평점
    private String backdropPath; // 배경 사진
    private String originalTitle; // 영화 원제
    private String movieTitle; // 영화 제목
    private LocalDateTime releaseDate; // 출시일
    private String posterPath; // 포스터 이미지
    private String overview; // 줄거리

    // 영화와 리뷰 (영화는 여러개의 리뷰를 가짐)
    @OrderBy("id")
//    @OrderBy("likes")
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @ToString.Exclude
    private final Set<Review> movieReviews = new LinkedHashSet<>();

    protected Movie () {}

    private Movie(Long movieId, Double rating, String backdropPath, String originalTitle, String movieTitle, LocalDateTime releaseDate, String posterPath, String overview) {
        this.movieId = movieId;
        this.rating = rating;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    public static Movie of(Long movieId, Double rating, String backdropPath, String originalTitle, String movieTitle, LocalDateTime releaseDate, String posterPath, String overview) {
        return new Movie(movieId, rating, backdropPath, originalTitle, movieTitle, releaseDate, posterPath, overview);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return id != null && id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
