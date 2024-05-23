package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.*;
import com.capstone.cinemate.Movie.repository.MemberMovieRepository;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.response.CustomResponse;
import com.capstone.cinemate.common.type.MovieSearchType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    @Value("${global.tmdb.access_token}")
    private String TMDB_ACCESS_TOKEN;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final MemberMovieRepository memberMovieRepository;
    private final GenreMemberRepository genreMemberRepository;
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
    public MovieWithReviewsDto getMovieWithReview(Long movieId) {
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
    public void saveMemberMovieSurveyResult(List<Long> movieIds, List<Long> genreIds, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        memberMovieRepository.deleteByMemberId(memberId);
        genreMemberRepository.deleteByMemberId(memberId);


        if (movieIds.size() > 3) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        movieIds.stream().limit(3).forEach(id->{
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(IllegalArgumentException::new);
            memberMovieRepository.save(new MemberMovie(member, movie));
        });

        genreIds.forEach(id-> {
            Genre genre = genreRepository.findById(id)
                    .orElseThrow(IllegalArgumentException::new);
            genreMemberRepository.save(new GenreMember(genre, member));
        });

        saveMemberMovieToMlServer(memberId, movieIds);

        // 설문조사 완료하면 true로 변경
        member.updateSurveyStatus(true);
        memberRepository.save(member);
    }

    public CustomResponse saveMemberMovieToMlServer(Long memberId, List<Long> movieIds) {
        // Set up the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", memberId);
        requestBody.put("movieIds", movieIds);

        // Set up the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Set up the request entity with headers and body
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Set up the REST template
        RestTemplate restTemplate = new RestTemplate();

        // Send the POST request
        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                ML_SERVER_URL + "/surveys/result",
                HttpMethod.POST,
                requestEntity,
                CustomResponse.class
        );

        // Return the response body
        return responseEntity.getBody();
    }

    // 영화 상세 정보 보기
    @Transactional(readOnly = true)
    public MovieDetailDto getMovieDetails(Long movieId) throws IOException, InterruptedException {
        MovieDto movieInfo = movieRepository.findById(movieId)
                .map(MovieDto::from)
                .orElseThrow(() -> new EntityNotFoundException("영화가 없습니다. - movieId: " + movieId));
        // TMDB
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/movie/" + movieInfo.movieId() + "/credits?language=ko-KR"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + TMDB_ACCESS_TOKEN)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        // JSON 파싱을 위한 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        Credit credit = objectMapper.readValue(response.body(), Credit.class);

        // crew의 posterPath에 https://image.tmdb.org/t/p/original를 추가하여 바로 이미지 접근이 가능한 url로 변경
        List<Cast> castList = credit.cast();
        List<Crew> crewList = credit.crew();
        for (Cast cast : castList) {
            String originalPosterPath = cast.getProfile_path();
            if (originalPosterPath == null)
                continue;
            String newPosterPath = "https://image.tmdb.org/t/p/original" + originalPosterPath;
            cast.setProfile_path(newPosterPath);
        }
        for (Crew crew : crewList) {
            String originalPosterPath = crew.getProfile_path();
            if (originalPosterPath == null)
                continue;
            String newPosterPath = "https://image.tmdb.org/t/p/original" + originalPosterPath;
            crew.setProfile_path(newPosterPath);
        }

        return new MovieDetailDto(movieInfo, credit);
    }

    // 영화 상세 정보 + 리뷰 까지 같이 보기
//    @Transactional(readOnly = true)
//    public MovieWithReviewsDto getMovieDetailWithReviews(Long movieId) {
//
//    }
}
