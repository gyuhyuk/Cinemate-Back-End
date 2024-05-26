package com.capstone.cinemate.Heart.service;

import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieHeartService {
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final MovieHeartRepository movieHeartRepository;

    @Transactional
    public boolean insertLikes(Long movieId, Long memberId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<MovieHeart> existingMovieHeart = movieHeartRepository.findByMemberAndMovie(member, movie);

        // 이미 좋아요가 눌려있으면
        if(existingMovieHeart.isPresent()) {
            movieHeartRepository.delete(existingMovieHeart.get());
            return false;
        }

        else {
            movieHeartRepository.save(new MovieHeart(member, movie));
            return true;
        }
    }
}
