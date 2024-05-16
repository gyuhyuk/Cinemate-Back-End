package com.capstone.cinemate.Review.service;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.dto.MovieReviewRequest;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Transactional
@Service
public class MovieReviewService {

    private final MovieRepository movieRepository;
    private final MovieReviewRepository movieReviewRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MovieReviewDto> searchMovieReview(Long movieId) {
        return List.of();
    }

    // 리뷰 등록
    @Transactional
    public void saveMovieReview(MovieReviewRequest movieReviewRequest, Long movieId, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Review review = Review.of(movie, movieReviewRequest.content(), member, movieReviewRequest.rating());
        movieReviewRepository.save(review);
    }

    // 리뷰 수정
    @Transactional
    public void updateMovieReview(MovieReviewRequest movieReviewRequest, Long movieId, Long reviewId, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        Review review = movieReviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateMember(review, member);

        Optional<Review> optionalReview = movieReviewRepository.findByMovie_IdAndId(movieId, reviewId);

        if(optionalReview.isEmpty()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        review.update(movie, movieReviewRequest.content(), movieReviewRequest.rating(), member);
        movieReviewRepository.save(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteMovieReview(Long reviewId, String memberId) {
        movieReviewRepository.deleteByIdAndMember_MemberId(reviewId, memberId);
    }

    private void validateMember(Review review, Member member) {
        if(!isMyReview(review,member)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    private boolean isMyReview(Review review, Member member){
        return review.getMember().equals(member);
    }
}
