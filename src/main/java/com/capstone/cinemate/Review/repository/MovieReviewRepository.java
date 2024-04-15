package com.capstone.cinemate.Review.repository;

import com.capstone.cinemate.Review.domain.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {
}