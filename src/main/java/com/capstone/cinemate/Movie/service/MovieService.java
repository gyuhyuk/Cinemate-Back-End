package com.capstone.cinemate.Movie.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import com.capstone.cinemate.Hate.repository.MovieHateRepository;
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
import org.springframework.core.ParameterizedTypeReference;
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
import java.nio.charset.StandardCharsets;
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
    private final MovieHateRepository movieHateRepository;

    // 영화 검색
    @Transactional(readOnly = true)
    public List<MovieDto> searchMoviesByPartialTitle(MovieSearchType movieSearchType, String searchValue, Long memberId) {
        List<Long> likedMovieIds = movieHeartRepository.findByMemberId(memberId)
                .stream()
                .map(movieHeart -> movieHeart.getMovie().getId())
                .toList();

        List<Long> dislikedMovieIds = movieHateRepository.findByMemberId(memberId)
                .stream()
                .map(movieHate -> movieHate.getMovie().getId())
                .toList();

        if (movieSearchType == MovieSearchType.TITLE) {
            String sanitizedSearchValue = searchValue.replaceAll("\\s+", ""); // 공백 제거
            return movieRepository.findByMovieTitleContaining(sanitizedSearchValue).stream()
                    .map(movie -> {
                        Long movieId = movie.getId();
                        boolean isLiked = likedMovieIds.contains(movieId);
                        boolean isDisliked = dislikedMovieIds.contains(movieId);
                        return MovieDto.from(movie, isLiked, isDisliked);
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
        Long[] VERSION_1_INDEX = {5503L, 3803L, 5104L, 2725L, 5738L, 4816L, 5779L, 4770L, 4549L, 4388L, 4846L, 4605L, 4764L, 5702L, 3650L, 1125L, 3974L, 4818L};

        Long[] VERSION_2_INDEX = {4789L, 4166L, 97L, 4755L, 106L, 3009L, 5078L, 5211L, 5544L, 3202L, 301L, 5101L, 4478L, 3096L, 3650L, 1125L, 6073L, 4733L};

        Long[] VERSION_3_INDEX = {4109L, 3800L, 4249L, 4717L, 4895L, 5738L, 4770L, 5211L, 3929L, 3202L, 5420L, 4605L, 4764L, 3096L, 3650L, 1125L, 4946L, 6637L};

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

        boolean hasExistingMemberMovies = !memberMovieRepository.findByMemberId(memberId).isEmpty(); // 값이 존재하면 true
        boolean hasExistingGenreMembers = !genreMemberRepository.findByMemberId(memberId).isEmpty(); // 값이 존재하면 true

        if (hasExistingMemberMovies && hasExistingGenreMembers) {
            return;
        }

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
                    Boolean isDisliked = movieHateRepository.existsByMemberIdAndMovieId(memberId, movieId);
                    return MovieDto.from(movieEntity, isLiked, isDisliked);
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
                    Boolean isDisliked = movieHateRepository.existsByMemberIdAndMovieId(memberId, movieId);
                    return MovieDto.from(movie1, isLiked, isDisliked);
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

    @Transactional(readOnly = true)
    public List<MovieDto> getRelatedMovieDetails(Long movieId, Long memberId) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ML_SERVER_URL+"/recommendation/movie/{movieId}")
                .buildAndExpand(movieId);

        // Header 제작
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(factory);

        CustomResponse<List<Long>> response = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<CustomResponse<List<Long>>>() {}).getBody();

        List<Long> movieIds = response.getData();
        List<MovieDto> movieDtos = new ArrayList<>();

        // 각 영화 ID에 대해 MovieDto를 만들어 리스트에 추가
        for (Long id : movieIds) {
            // movieRepository를 사용하여 영화 정보를 가져옴
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));

            // MovieDto 생성 시 좋아요 여부를 설정
            boolean liked = movieHeartRepository.existsByMemberIdAndMovieId(memberId, id);
            boolean disliked = movieHateRepository.existsByMemberIdAndMovieId(memberId, id);
            MovieDto movieDto = MovieDto.from(movie, liked, disliked);

            // movieDtos 리스트에 추가
            movieDtos.add(movieDto);
        }

        return movieDtos;
    }




}