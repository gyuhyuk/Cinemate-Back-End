package com.capstone.cinemate.Review.service;

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

    // 마이페이지 내 리뷰 조회
    @Transactional(readOnly = true)
    public List<MovieReviewDto> searchMyReview(Long memberId) {
        List<Review> reviews = movieReviewRepository.findByMember_Id(memberId);

        List<MovieReviewDto> movieReviewDtos = reviews.stream()
                .map(MovieReviewDto::from)
                .toList();

        return movieReviewDtos;
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public List<MovieReviewDto> searchMovieReview(Long movieId, String criteria) {
        return switch (criteria) {
            case "like" -> movieReviewRepository.findByMovieIdOrderByLikesDesc(movieId).stream().map(MovieReviewDto::from).toList();
            case "old" -> movieReviewRepository.findByMovieIdOrderByCreatedAtAsc(movieId).stream().map(MovieReviewDto::from).toList();
            case "grade" -> movieReviewRepository.findByMovieIdOrderByRatingDesc(movieId).stream().map(MovieReviewDto::from).toList();
            default -> movieReviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId).stream().map(MovieReviewDto::from).toList();
        };
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
            return MovieReviewDto.from(updatedReview);
        } else {
            Review newReview = Review.of(movie, member, movieReviewRatingRequest.getRating());
            Review savedReview = movieReviewRepository.save(newReview);
            return MovieReviewDto.from(savedReview);
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
            return MovieReviewDto.from(savedReview);
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

        return MovieReviewDto.from(updatedReview);
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
