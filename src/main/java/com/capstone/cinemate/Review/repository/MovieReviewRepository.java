package com.capstone.cinemate.Review.repository;

import com.capstone.cinemate.Review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie_Id(Long movieId);
    void deleteByIdAndMember_MemberId(Long reviewId, String MemberId);
}