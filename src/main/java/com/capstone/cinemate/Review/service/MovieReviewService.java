package com.capstone.cinemate.Review.service;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieWithReviewsDto;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.dto.MovieReviewRequest;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    public void saveMovieReview(MovieReviewRequest movieReviewRequest, Long movieId, Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));

        Review review = Review.of(movie, movieReviewRequest.content(), member, movieReviewRequest.rating());
        movieReviewRepository.save(review);
    }

    // 리뷰 수정
    public void updateMovieReview(MovieReviewDto dto) {

    }

    // 리뷰 삭제
    public void deleteMovieReview(Long reviewId, String memberId) {
        movieReviewRepository.deleteByIdAndMember_MemberId(reviewId, memberId);
    }
}
