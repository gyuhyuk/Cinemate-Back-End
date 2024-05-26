package com.capstone.cinemate.Heart.service;

import com.capstone.cinemate.Heart.domain.ReviewHeart;
import com.capstone.cinemate.Heart.dto.ReviewHeartRequestDto;
import com.capstone.cinemate.Heart.repository.ReviewHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewHeartService {

    private final ReviewHeartRepository reviewHeartRepository;
    private final MemberRepository memberRepository;
    private final MovieReviewRepository movieReviewRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public boolean insertLikes(Long movieId, Long reviewId, Long memberId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = movieReviewRepository.findByMovie_IdAndId(movieId, reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(review.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.SELF_LIKE_NOT_ALLOWED);
        }

        Optional<ReviewHeart> existingReviewHeart = reviewHeartRepository.findByMemberAndMovieAndReview(member, movie, review);

        // 이미 좋아요가 눌려있으면
        if(existingReviewHeart.isPresent()) {
            review.decreaseLikes();
            reviewHeartRepository.delete(existingReviewHeart.get());
            return false;
        }

        else {
            review.increaseLikes();
            reviewHeartRepository.save(new ReviewHeart(member, movie, review));
            return true;
        }
    }

//    @Transactional(readOnly = true)
//    public
}
