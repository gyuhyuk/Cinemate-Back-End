package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import com.capstone.cinemate.Heart.domain.MovieHeart;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Heart.repository.ReviewHeartRepository;
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
    private final ReviewHeartRepository reviewHeartRepository;

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
        Long[] VERSION_1_INDEX = {5501L, 3801L, 5102L, 2723L, 5736L, 4814L, 5777L, 4768L, 4547L, 4386L, 4844L, 4603L, 4762L, 5700L, 3648L, 1123L, 3972L, 4816L};

        Long[] VERSION_2_INDEX = {4787L, 4164L, 95L, 4753L, 104L, 3007L, 5076L, 5209L, 5542L, 3200L, 299L, 5099L, 4476L, 3094L, 3648L, 1123L, 6071L, 4731L};

        Long[] VERSION_3_INDEX = {4107L, 3798L, 4247L, 4715L, 4893L, 5736L, 4768L, 5209L, 3927L, 3200L, 5418L, 4603L, 4762L, 3094L, 3648L, 1123L, 4944L, 6635L};

        int randomVersion = new Random().nextInt(3)+1; // 1, 2, 3번 버전의 인덱스

        Long[] movieIds = switch (randomVersion) {
            case 1 -> VERSION_1_INDEX;
            case 2 -> VERSION_2_INDEX;
            case 3 -> VERSION_3_INDEX;
            default -> throw new IllegalStateException("Unexpected value: " + randomVersion);
        };

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


        if (movieIds.size() > 6) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        movieIds.stream().limit(6).forEach(id->{
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
        // request body 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", memberId);
        requestBody.put("movieIds", movieIds);

        // HTTP header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 헤더와 바디로 requestEntity 설정
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Rest Template 설정
        RestTemplate restTemplate = new RestTemplate();

        // Post request 설정
        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                ML_SERVER_URL + "/surveys/result",
                HttpMethod.POST,
                requestEntity,
                CustomResponse.class
        );

        // response body 리턴
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

    @Transactional(readOnly = true)
    public MovieWithReviewsDto getMovieWithReviews(Long movieId, Long memberId) {
        MovieDto movie = movieRepository.findById(movieId)
                .map(movie1 -> {
                    Boolean isLiked = movieHeartRepository.existsByMemberIdAndMovieId(memberId, movieId);
                    return MovieDto.from(movie1, isLiked);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Review> reviews = movieReviewRepository.findByMovie_Id(movieId);

        // Review 객체를 MovieReviewDto 객체로 변환할 때, 현재 memberId와 리뷰 작성자의 ID를 비교하여 isMine 값을 설정
        Set<MovieReviewDto> movieReviewDtos = reviews.stream()
                .map(review -> MovieReviewDto.from(review, review.getMember().getId().equals(memberId), reviewHeartRepository.findByMember_IdAndMovie_IdAndReview_Id(memberId, movieId, review.getId()).isPresent())) // 수정된 부분
                .collect(Collectors.toSet());

        return new MovieWithReviewsDto(movieReviewDtos, movieId, movie.rating(), movie.backdropPath(), movie.originalTitle(),
                movie.movieTitle(), movie.releaseDate(), movie.posterPath(),
                movie.overview(), movie.isLiked());
    }

}