package com.capstone.cinemate.Review.service;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieWithReviewsDto;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
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
//    public void saveMovieReview(MovieReviewDto dto) {
//        Movie movie = movieRepository.findById(dto.movieId())
//                .orElseThrow(() -> new EntityNotFoundException("영화를 찾을 수 없습니다."));
////        Member member = memberRepository.getReferenceById(dto.memberDto().memberId());
//        Review review = dto.toEntity(movie, member);
//        movieReviewRepository.save(review);
//    }

    // 리뷰 수정
    public void updateMovieReview(MovieReviewDto dto) {

    }

    // 리뷰 삭제
    public void deleteMovieReview(Long reviewId, String memberId) {
        movieReviewRepository.deleteByIdAndMember_MemberId(reviewId, memberId);
    }
}
