package com.capstone.cinemate.Hate.service;

import com.capstone.cinemate.Hate.domain.MovieHate;
import com.capstone.cinemate.Hate.repository.MovieHateRepository;
import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import com.capstone.cinemate.Movie.dto.MoviesResponse;
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
public class MovieHateService {

    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final MovieHateRepository movieHateRepository;
    private final MovieHeartRepository movieHeartRepository;

    // 관심없는 영화 return
    @Transactional(readOnly = true)
    public MoviesResponse getDislikeMovies(Long memberId) {
        List<MovieResponse> moviesResponses = movieHateRepository.findHateMoviesByMemberId(memberId).stream()
                .map(MovieResponse::of)
                .toList();
        return new MoviesResponse(moviesResponses);
    }

    // 관심없는 영화 추가 및 제거
    @Transactional
    public boolean insertDislikes(Long movieId, Long memberId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<MovieHate> existingMovieHate = movieHateRepository.findByMemberAndMovie(member, movie);
        Optional<MovieHeart> existingMovieHeart = movieHeartRepository.findByMemberAndMovie(member, movie);

        // TODO : 좋아요에 없고 싫어요에 없음 -> 싫어요 DB에 추가
        // TODO : 좋아요에 있고 싫어요에 없음 -> 좋아요 DB 제거, 싫어요 DB에 추가
        // TODO : 좋아요에 없고 싫어요에 있음 -> 싫어요 DB 제거
        // TODO : 좋아요에 있고 싫어요에 있음 -> 존재할 수 없음

        if(existingMovieHeart.isEmpty() && existingMovieHate.isEmpty()) {
            movieHateRepository.save(new MovieHate(member, movie));
        } else if (existingMovieHeart.isPresent() && existingMovieHate.isEmpty()) {
            movieHeartRepository.delete(existingMovieHeart.get());
            movieHateRepository.save(new MovieHate(member, movie));
        } else if (existingMovieHeart.isEmpty() && existingMovieHate.isPresent()) {
            movieHateRepository.delete(existingMovieHate.get());
        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }


        saveMovieHateToMlServer(memberId, movieId);
        return existingMovieHate.isEmpty();
    }

    // ml 서버에게 멤버에 따른 관심없음 영화 post요청
    public CustomResponse saveMovieHateToMlServer(Long memberId, Long movieId) {
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
                ML_SERVER_URL + "/dislikes",
                HttpMethod.POST,
                requestEntity,
                CustomResponse.class
        );

        // Return the response body
        return responseEntity.getBody();
    }
}
