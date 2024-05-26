package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.*;
import com.capstone.cinemate.Movie.repository.MemberMovieRepository;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.dto.MovieReviewDto;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieHeartRepository movieHeartRepository;
    private final MovieReviewRepository movieReviewRepository;
    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    @Value("${global.tmdb.access_token}")
    private String TMDB_ACCESS_TOKEN;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final MemberMovieRepository memberMovieRepository;
    private final GenreMemberRepository genreMemberRepository;
    private final MemberRepository memberRepository;

    // 영화 검색
    @Transactional(readOnly = true)
    public List<MovieDto> searchMoviesByPartialTitle(MovieSearchType movieSearchType, String searchValue, Long memberId) {
        List<Long> likedMovieIds = movieHeartRepository.findByMemberId(memberId)
                .stream()
                .map(movieHeart -> movieHeart.getMovie().getId())
                .toList();

        if (movieSearchType == MovieSearchType.TITLE) {
            String sanitizedSearchValue = searchValue.replaceAll("\\s+", ""); // 공백 제거
            return movieRepository.findByMovieTitleContaining(sanitizedSearchValue).stream()
                    .map(movie -> {
                        Long movieId = movie.getId();
                        boolean isLiked = likedMovieIds.contains(movieId);
//                        System.out.println("Movie ID: " + movieId + ", isLiked: " + isLiked); // 디버깅 메시지
                        return MovieDto.from(movie, isLiked);
                    })
                    .collect(Collectors.toList());
        } else {
            // 다른 검색 유형에 대한 처리 추가 가능
            return Collections.emptyList(); // 다른 검색 유형을 처리하지 않는 경우 빈 리스트 반환
        }
    }

    // 멤버가 저장한 영화 보기
    @Transactional(readOnly = true)
    public MoviesResponse getMemberMovies(Long memberId) {
        List<MovieResponse> movieResponses = memberMovieRepository.findMemberMoviesByMemberId(memberId).stream()
                .map(MovieResponse::of)
                .toList();
        return new MoviesResponse(movieResponses);
    }

    @Transactional(readOnly = true)
    public MoviesResponse getRandomMovies() {
        Long[] VERSION_1_INDEX = {5638L, 3916L, 5233L, 2825L, 5876L, 4942L, 5919L, 4895L, 4671L, 4508L, 2061L, 4727L, 4888L, 5840L, 3760L, 4092L, 6215L, 4944L};
        // Version 2 Index
        Long[] VERSION_2_INDEX = {4914L, 4285L, 100L, 4879L, 109L, 3113L, 5205L, 5342L, 5679L, 3310L, 310L, 5230L, 4599L, 3202L, 3760L, 4092L, 5072L, 4857L};
        // Version 3 Index
        Long[] VERSION_3_INDEX = {4227L, 3913L, 4368L, 4841L, 5021L, 5876L, 4895L, 5342L, 4047L, 3310L, 5552L, 4727L, 4888L, 3202L, 3760L, 4092L, 6511L, 4608L};

        int randomVersion = new Random().nextInt(3)+1;

        Long[] movieIds;
        switch (randomVersion) {
            case 1:
                movieIds = VERSION_1_INDEX;
                break;
            case 2:
                movieIds = VERSION_2_INDEX;
                break;
            case 3:
                movieIds = VERSION_3_INDEX;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + randomVersion);
        }

        List<Movie> movies = movieRepository.findAllById(List.of(movieIds));

        List<MovieResponse> movieResponses = movies.stream()
                .map(movie -> new MovieResponse(movie.getId(),
                        movie.getMovieId(),
                        movie.getRating(),
                        movie.getBackdropPath(),
                        movie.getOriginalTitle(),
                        movie.getMovieTitle(),
                        movie.getReleaseDate(),
                        movie.getPosterPath(),
                        movie.getOverview()))
                .collect(Collectors.toList());

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

    // 영화 상세 정보 보기 (배우 + 감독진)
    @Transactional(readOnly = true)
    public MovieDetailDto getMovieDetails(Long memberId, Long movieId) throws IOException, InterruptedException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_USER));
        MovieDto movieInfo = movieRepository.findById(movieId)
                .map(movieEntity -> {
                    Boolean isLiked = movieHeartRepository.existsByMemberIdAndMovieId(memberId, movieId);
                    return MovieDto.from(movieEntity, isLiked);
                })
                .orElseThrow(() -> new EntityNotFoundException("영화가 없습니다. - movieId: " + movieId));
        boolean isLiked = movieHeartRepository.existsByMemberIdAndMovieId(memberId, movieId);

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
    @Transactional(readOnly = true)
    public MovieWithReviewsDto getMovieWithReviews(Long movieId, Long memberId) {
        MovieDto movie = movieRepository.findById(movieId)
                .map(movie1 -> {
                    Boolean isLiked = movieHeartRepository.existsByMemberIdAndMovieId(memberId, movieId);
                    return MovieDto.from(movie1, isLiked);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Review> reviews = movieReviewRepository.findByMovie_Id(movieId);

        Set<MovieReviewDto> movieReviewDtos = reviews.stream()
                .map(MovieReviewDto::from) // MovieReviewDto의 from 메소드를 사용하여 변환
                .collect(Collectors.toSet());

        return new MovieWithReviewsDto(movieReviewDtos, movieId, movie.rating(), movie.backdropPath(), movie.originalTitle(),
                movie.movieTitle(), movie.releaseDate(), movie.posterPath(),
                movie.overview(), movie.isLiked());
    }
}
