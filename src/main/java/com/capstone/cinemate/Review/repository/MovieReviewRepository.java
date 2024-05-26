package com.capstone.cinemate.Review.repository;

import com.capstone.cinemate.Review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie_Id(Long movieId);
//    @Query("SELECT r FROM Review r JOIN r.movie m WHERE m.id = :movieId AND r.id = :reviewId")
    Optional<Review> findByMovie_IdAndId(Long movieId, Long reviewId);
    List<Review> findByMember_Id(Long memberId);

    Optional<Review> findByIdAndMember_Id(Long reviewId, Long memberId);
    boolean existsByMovie_IdAndMember_Id(Long movieId, Long memberId);
    Optional<Review> findByMovie_IdAndMember_Id(Long movieId, Long memberId);
    Optional<Review> findByMovie_IdAndMember_IdAndId(Long movieId, Long memberId, Long reviewId);
    void deleteByIdAndMember_Id(Long reviewId, Long MemberId);
}