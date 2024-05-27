package com.capstone.cinemate.Review.service;

import com.capstone.cinemate.Heart.repository.ReviewHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.dto.MovieReviewContentRequest;
import com.capstone.cinemate.Review.dto.MovieReviewRatingRequest;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional
@Service
public class MovieReviewService {

    private final MovieRepository movieRepository;
    private final MovieReviewRepository movieReviewRepository;
    private final MemberRepository memberRepository;
    private final ReviewHeartRepository reviewHeartRepository;

    // 마이페이지 내 리뷰 조회
    @Transactional(readOnly = true)
    public List<MovieReviewDto> searchMyReview(Long memberId) {
        List<Review> reviews = movieReviewRepository.findByMember_Id(memberId);

        List<MovieReviewDto> movieReviewDtos = reviews.stream()
                .map(review -> MovieReviewDto.from(review, true, false)) // isMine을 true로 설정
                .toList();

        return movieReviewDtos;
    }

    @Transactional(readOnly = true)
    public List<MovieReviewDto> searchMovieReview(Long movieId, Long memberId, String criteria) {
        List<Review> reviews;
        switch (criteria) {
            case "like":
                reviews = movieReviewRepository.findByMovieIdOrderByLikesDesc(movieId);
                break;
            case "old":
                reviews = movieReviewRepository.findByMovieIdOrderByCreatedAtAsc(movieId);
                break;
            case "grade":
                reviews = movieReviewRepository.findByMovieIdOrderByRatingDesc(movieId);
                break;
            case "latest":
                reviews = movieReviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
                break;
            default:
                return List.of();
        }


        return reviews.stream()
                .filter(review -> !StringUtils.isEmpty(review.getContent())) // 리뷰 내용이 비어있지 않은 경우만 필터링
                .map(review -> MovieReviewDto.from(review, review.getMember().getId().equals(memberId),
                        reviewHeartRepository.findByMember_IdAndMovie_IdAndReview_Id(memberId, movieId, review.getId()).isPresent()))
                .collect(Collectors.toList());
    }


    // 리뷰 별점 받아오기
    @Transactional
    public Double getRating(Long movieId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Optional<Review> optionalReview = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId);

        return optionalReview.get().getRating();
    }

    // 리뷰 별점 등록 및 수정
    @Transactional
    public MovieReviewDto createOrUpdateMovieRating(MovieReviewRatingRequest movieReviewRatingRequest, Long movieId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Optional<Review> optionalReview = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId);
        Review review;

        // 별점이 0점일 때 어떻게 할 지 생각해보기
//        if (movieReviewRatingRequest.getRating() == 0) {
//            // 별점이 0인 경우 리뷰를 삭제
//            if (optionalReview.isPresent()) {
//                review = optionalReview.get();
//                validateMember(review, member);
//                movieReviewRepository.delete(review);
//                return null; // 삭제 후 반환할 DTO가 없으므로 null 반환
//            } else {
//                throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
//            }
//        }

        if (optionalReview.isPresent()) {
            // 리뷰가 존재하는 경우, 업데이트
            review = optionalReview.get();
            validateMember(review, member);
            review.ratingUpdate(movie, movieReviewRatingRequest.getRating(), member);
            Review updatedReview = movieReviewRepository.save(review);
            return MovieReviewDto.from(updatedReview, true, false);
        } else {
            Review newReview = Review.of(movie, member, movieReviewRatingRequest.getRating());
            Review savedReview = movieReviewRepository.save(newReview);
            return MovieReviewDto.from(savedReview, true, false);
        }
    }

    // 리뷰 내용 등록
    @Transactional
    public MovieReviewDto saveMovieContent(MovieReviewContentRequest movieReviewRequest, Long movieId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        // movie에 member가 작성한 review 조회
        Review review = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(review.getContent().isEmpty()) {
            review.setContent(movieReviewRequest.getContent());
            Review savedReview = movieReviewRepository.save(review);
            return MovieReviewDto.from(savedReview, true, false);
        } else {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }

    // 리뷰 내용 수정
    @Transactional
    public MovieReviewDto updateMovieContent(MovieReviewContentRequest movieReviewRequest, Long movieId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        Review review = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId).orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        validateMember(review, member);

        Optional<Review> optionalReview = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId);

        if(optionalReview.isEmpty()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        review.contentUpdate(movie, movieReviewRequest.getContent(), member);
        Review updatedReview = movieReviewRepository.save(review);

        return MovieReviewDto.from(updatedReview, true, false);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteMovieReview(Long movieId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        Review review = movieReviewRepository.findByMovie_IdAndMember_Id(movieId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        movieReviewRepository.delete(review);
    }

    // 리뷰 수정 validation
    private void validateMember(Review review, Member member) {
        if(!isMyReview(review, member)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    // 자신의 리뷰인지 확인
    private boolean isMyReview(Review review, Member member){
        return review.getMember().equals(member);
    }
}
