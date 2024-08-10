package com.capstone.cinemate.Hate.service;

import com.capstone.cinemate.Hate.repository.MovieHateRepository;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieHateService {

    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final MovieHateRepository movieHateRepository;

    // 싫어하는 영화 return
    @Transactional(readOnly = true)
    public MoviesResponse getDislikeMovies(Long memberId) {
        List<MovieResponse> moviesResponses = movieHateRepository.findHateMoviesByMemberId(memberId).stream()
                .map(MovieResponse::of)
                .toList();
        return new MoviesResponse(moviesResponses);
    }
}
