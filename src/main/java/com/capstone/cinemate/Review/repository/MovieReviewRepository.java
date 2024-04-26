package com.capstone.cinemate.Review.repository;

import com.capstone.cinemate.Review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieReviewRepository extends JpaRepository<Review, Long> {
}