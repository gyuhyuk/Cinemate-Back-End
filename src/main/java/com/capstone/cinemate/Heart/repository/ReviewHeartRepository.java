package com.capstone.cinemate.Heart.repository;

import com.capstone.cinemate.Heart.domain.ReviewHeart;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewHeartRepository extends JpaRepository<ReviewHeart, Long> {
    Optional<ReviewHeart> findByMemberAndMovieAndReview(Member member, Movie movie, Review review);
}
