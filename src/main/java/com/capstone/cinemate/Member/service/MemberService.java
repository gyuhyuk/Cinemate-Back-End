package com.capstone.cinemate.Member.service;

import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Heart.repository.MovieHeartRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.*;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.dto.MovieListWithGenreId;
import com.capstone.cinemate.Movie.dto.MovieResponse;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.Review.domain.Review;
import com.capstone.cinemate.Review.repository.MovieReviewRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.jwt.TokenUtils;
import com.capstone.cinemate.common.response.CustomResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${global.ml_server.url}")
    private String ML_SERVER_URL;
    private final MemberRepository memberRepository;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final MovieRepository movieRepository;
    private final GenreMemberRepository genreMemberRepository;
    private final MovieHeartRepository movieHeartRepository;
    private final MovieReviewRepository movieReviewRepository;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) throws CustomException {
        if (memberRepository.findByNickName(signUpRequest.getNickName()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME_RESOURCE);
        }
        if (memberRepository.findByMemberId(signUpRequest.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL_RESOURCE);
        }
        Member member = memberRepository.save(
                Member.builder()
                        .password(passwordEncoder.encode(signUpRequest.getPassword()))
                        .memberId(signUpRequest.getEmail())
                        .nickName(signUpRequest.getNickName())
                        .build());

        return new SignUpResponse(member.getMemberId(), member.getNickName());
    }

    @Transactional
    public TokenResponse signIn(LoginRequest loginRequest) throws CustomException {
        Member member = memberRepository
                .findByMemberId(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!hasSamePassword(member, loginRequest)) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        String accessToken = tokenUtils.generateJwtToken(member);
        return new TokenResponse(accessToken, member.getSurvey());
    }

    private boolean hasSamePassword(Member member, LoginRequest loginRequest) {
        return passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
    }

    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    public boolean checkNicknameDuplicate(String nickName) {
        return memberRepository.existsByNickName(nickName);
    }

    public Member findMemberByJwt(final String jwt) {
        Claims claims = tokenUtils.getClaimsFormToken(jwt);
        String memberId = claims.get("memberId", String.class);

        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public RecommendationResponse recommend(Long memberId) {
        // 기본 추천
        CustomResponse<List<Long>> defaultRecommendResponse = recommendRequest(memberId);

        List<Long> defaultRecommendIdResult = defaultRecommendResponse.getData();

        // 멤버의 장르 ID 리스트를 가져옴
        List<Long> genreIdList = genreMemberRepository.findGenreIdsByMemberId(memberId).stream()
                .map(id -> ((Number) id).longValue())
                .toList();

        // 장르별 영화가 묶인 리스트의 전체 리스트
        return getRecommendationResponse(memberId, genreIdList, defaultRecommendIdResult);
    }

    private RecommendationResponse getRecommendationResponse(Long memberId, List<Long> genreIdList, List<Long> defaultRecommendIdResult) {
        List<MovieListWithGenreId> genreMovieLists = new ArrayList<>();

        for (Long id : genreIdList) {
            List<Long> genreMovieIdList = fetchGenreRecommendations(memberId, id);
            MovieListWithGenreId movieListWithGenreId = new MovieListWithGenreId();
            movieListWithGenreId.setGenreId(id);
            movieListWithGenreId.setMovieList(movieRepository.findAllByIdIn(genreMovieIdList));
            genreMovieLists.add(movieListWithGenreId);
        }

        // 영화 ID 리스트를 영화 상세 정보로 변환
        List<MovieResponse> defaultRecommendResult = movieRepository.findAllByIdIn(defaultRecommendIdResult);

        // 결과 반환
        RecommendationResponse response = new RecommendationResponse();
        response.setDefaultRecommendResult(defaultRecommendResult);
        response.setGenreMovieLists(genreMovieLists);

        return response;
    }

    // 추천하는 장르 fetch
    private List<Long> fetchGenreRecommendations(Long memberId, Long genreId) {
        CustomResponse<List<Long>> genreMovieIdResponse = recommendRequest(memberId, genreId);

        return genreMovieIdResponse.getData();
    }

    public CustomResponse<List<Long>> recommendRequest(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Long userId = member.getId();

        // query parameter 방식 사용
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ML_SERVER_URL+"/recommendation")
                .queryParam("userId", userId)
                .build(false);

        // Header 제작
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(factory);

        // 응답
        return restTemplate.getForObject(uriComponents.toUriString(), CustomResponse.class);
    }

    public CustomResponse<List<Long>> recommendRequest(Long memberId, Long genreId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Long userId = member.getId();
        // query parameter 방식 사용
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ML_SERVER_URL+"/recommendation")
                .queryParam("userId", userId)
                .queryParam("genreId", genreId)
                .build(false);

        // Header 제작
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(factory);

        // 응답
        return restTemplate.getForObject(uriComponents.toUriString(), CustomResponse.class);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<Movie> likeMovies = movieHeartRepository.findLikeMoviesByMemberId(memberId);
        List<Review> myReviews = movieReviewRepository.findByMember_Id(memberId);

        long validateReviewCount = myReviews.stream().filter(review ->
            review.getContent() != null && !review.getContent().isEmpty()).count();

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", member.getNickName());
        result.put("likeMovies", likeMovies.size());
        result.put("myReviews", (int) validateReviewCount);

        return result;
    }
}
