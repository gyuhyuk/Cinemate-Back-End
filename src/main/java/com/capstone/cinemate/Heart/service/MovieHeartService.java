package com.capstone.cinemate.Heart.service;

import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import com.capstone.cinemate.Movie.dto.MovieWithoutMovieIdResponse;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
import com.capstone.cinemate.Movie.dto.MoviesWithoutMovieIdResponse;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.response.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieHeartService {
    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final MovieHeartRepository movieHeartRepository;

    @Transactional(readOnly = true)
    public MoviesResponse getLikeMovies(Long memberId) {
        List<MovieResponse> movieResponses = movieHeartRepository.findLikeMoviesByMemberId(memberId).stream()
                .map(MovieResponse::of)
                .toList();
        return new MoviesResponse(movieResponses);
    }
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
        }

        else {
            movieHeartRepository.save(new MovieHeart(member, movie));
        }

        saveMovieHeartToMlServer(memberId, movieId);
        return existingMovieHeart.isEmpty();
    }


    public CustomResponse saveMovieHeartToMlServer(Long memberId, Long movieId) {
        // Set up the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", memberId);
        requestBody.put("movieId", movieId);

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up the request entity with headers and body
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Set up the REST template
        RestTemplate restTemplate = new RestTemplate();

        // Send the POST request
        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                ML_SERVER_URL + "/likes",
                HttpMethod.POST,
                requestEntity,
                CustomResponse.class
        );

        // Return the response body
        return responseEntity.getBody();
    }
}
