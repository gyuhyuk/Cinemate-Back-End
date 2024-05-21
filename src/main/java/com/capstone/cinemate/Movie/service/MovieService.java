package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieDto;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import com.capstone.cinemate.Movie.dto.MovieWithReviewsDto;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.Movie.repository.MemberMovieRepository;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.type.MovieSearchType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final MemberMovieRepository memberMovieRepository;
    private final MemberRepository memberRepository;


    // 전체 영화 return
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(MovieDto::from)
                .collect(Collectors.toList());
    }

    // 영화 검색
    @Transactional(readOnly = true)
    public List<MovieDto> searchMoviesByPartialTitle(MovieSearchType movieSearchType, String searchValue) {
        if (movieSearchType == MovieSearchType.TITLE) {
            return movieRepository.findByMovieTitleContaining(searchValue).stream()
                    .map(MovieDto::from)
                    .collect(Collectors.toList());
        } else {
            // 다른 검색 유형에 대한 처리 추가 가능
            return Collections.emptyList(); // 다른 검색 유형을 처리하지 않는 경우 빈 리스트 반환
        }
    }

    // 영화 정보 입력 시 리뷰 까지 같이 조회
    @Transactional(readOnly = true)
    public MovieWithReviewsDto getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .map(MovieWithReviewsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("영화가 없습니다. - movieId: " + movieId));
    }

    // 멤버가 저장한 영화 보기
    @Transactional(readOnly = true)
    public MoviesResponse getMemberMovies(Long memberId) {
        List<MovieResponse> movieResponses = memberMovieRepository.findMemberMoviesByMemberId(memberId).stream()
                .map(MovieResponse::of)
                .toList();
        return new MoviesResponse(movieResponses);
    }

    // 멤버가 영화 저장하기
    @Transactional
    public void saveMemberMovie(List<Long> movieIds, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        memberMovieRepository.deleteByMemberId(memberId);


        if (movieIds.size() > 3) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        movieIds.stream().limit(3).forEach(id->{
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(IllegalArgumentException::new);
            memberMovieRepository.save(new MemberMovie(member, movie));
        });

        // 설문조사 완료하면 true로 변경
        member.updateSurveyStatus(true);
        memberRepository.save(member);
    }
}
