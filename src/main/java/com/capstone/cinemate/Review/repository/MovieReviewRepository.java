package com.capstone.cinemate.Review.repository;

import com.capstone.cinemate.Review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie_Id(Long movieId);
    Optional<Review> findByMovie_IdAndId(Long movieId, Long reviewId);
    List<Review> findByMember_Id(Long memberId);
    Optional<Review> findByMovie_IdAndMember_Id(Long movieId, Long memberId);

    Page<Review> findAll(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.likes DESC")
    List<Review> findByMovieIdOrderByLikesDesc(@Param("movieId") Long movieId);

    // 특정 영화의 리뷰를 만들어진 날짜로 정렬
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.createdAt DESC")
    List<Review> findByMovieIdOrderByCreatedAtDesc(@Param("movieId") Long movieId);
    // 특정 영화의 리뷰를 평점 순으로 정렬
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.rating DESC")
    List<Review> findByMovieIdOrderByRatingDesc(@Param("movieId") Long movieId);

    // 특정 영화의 리뷰를 수정된 날짜 오름차순으로 정렬
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId ORDER BY r.createdAt ASC")
    List<Review> findByMovieIdOrderByCreatedAtAsc(@Param("movieId") Long movieId);
}