package com.capstone.cinemate.Member.service;

import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.*;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.Movie.repository.MovieRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.jwt.TokenUtils;
import com.capstone.cinemate.common.response.CustomResponse;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${ml_server.url}")
    private String ML_SERVER_URL;
    private final MemberRepository memberRepository;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final MovieRepository movieRepository;
    private final GenreMemberRepository genreMemberRepository;

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

    public RecommendationResponse recommend(Long memberId, Long genreId) {
        if (genreId == null) {
            return recommend(memberId); // 기존의 단일 인자를 처리하는 메서드 호출
        }

        // 기본 추천
        CustomResponse<List<Long>> defaultRecommendResponse = recommendRequest(memberId);
        List<Long> defaultRecommendIdResult = defaultRecommendResponse.getData();

        // 멤버의 장르 ID 리스트를 가져옴
        List<Long> genreIdList = genreMemberRepository.findGenreIdsByMemberId(memberId);

        // 장르별 영화가 묶인 리스트의 전체 리스트
        List<List<Movie>> genreMovieLists = new ArrayList<>();

        for (Long id : genreIdList) {
            List<Long> genreMovieIdList = fetchGenreRecommendations(memberId, id);
            genreMovieLists.add(movieRepository.findAllByIdIn(genreMovieIdList));
        }

        // 영화 ID 리스트를 영화 상세 정보로 변환
        List<Movie> defaultRecommendResult = movieRepository.findAllByIdIn(defaultRecommendIdResult);

        // 결과 반환
        RecommendationResponse response = new RecommendationResponse();
        response.setDefaultRecommendResult(defaultRecommendResult);
        response.setGenreMovieLists(genreMovieLists);

        return response;
    }


    public RecommendationResponse recommend(Long memberId) {
        // 기본 추천
        CustomResponse<List<Long>> defaultRecommendResponse = recommendRequest(memberId);
        List<Long> defaultRecommendIdResult = defaultRecommendResponse.getData();

        for (int i = 0; i < defaultRecommendIdResult.size(); i++) {
            defaultRecommendIdResult.set(i, defaultRecommendIdResult.get(i));
        }

        // 멤버의 장르 ID 리스트를 가져옴
        List<Long> genreIdList = genreMemberRepository.findGenreIdsByMemberId(memberId).stream()
                .map(id -> ((Number) id).longValue())
                .toList();



        // 장르별 영화가 묶인 리스트의 전체 리스트
        List<List<Movie>> genreMovieLists = new ArrayList<>();


        for (Long genreId : genreIdList) {
            List<Long> genreMovieIdList = fetchGenreRecommendations(memberId, genreId);
            genreMovieLists.add(movieRepository.findAllByIdIn(genreMovieIdList));
        }

        // 영화 ID 리스트를 영화 상세 정보로 변환
        List<Movie> defaultRecommendResult = movieRepository.findAllByIdIn(defaultRecommendIdResult);

        // 결과 반환
        RecommendationResponse response = new RecommendationResponse();
        response.setDefaultRecommendResult(defaultRecommendResult);
        response.setGenreMovieLists(genreMovieLists);

        return response;
    }

    // 추천하는 장르 fetch
    private List<Long> fetchGenreRecommendations(Long memberId, Long genreId) {
        CustomResponse<List<Long>> genreMovieIdResponse = recommendRequest(memberId, genreId);
        List<Long> genreMovieIdList = genreMovieIdResponse.getData();
        for (int i = 0; i < genreMovieIdList.size(); i++) {
            genreMovieIdList.set(i, genreMovieIdList.get(i));
        }
        return genreMovieIdList;
    }

    public CustomResponse recommendRequest(Long memberId) {
        // query parameter 방식 사용
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ML_SERVER_URL+"/recommendation")
                .queryParam("userId", memberId)
                .build(false);

        // Header 제작
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectTimeout(5000); // api 호출 타임아웃
//        factory.setReadTimeout(5000); // api 읽기 타임아웃

        RestTemplate restTemplate = new RestTemplate(factory);

        // 응답
        return restTemplate.getForObject(uriComponents.toUriString(), CustomResponse.class);
    }

    public CustomResponse recommendRequest(Long memberId, Long genreId) {
        // query parameter 방식 사용
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ML_SERVER_URL+"/recommendation")
                .queryParam("userId", memberId)
                .queryParam("genreId", genreId)
                .build(false);

        // Header 제작
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectTimeout(5000); // api 호출 타임아웃
//        factory.setReadTimeout(5000); // api 읽기 타임아웃

        RestTemplate restTemplate = new RestTemplate(factory);

        // 응답
        return restTemplate.getForObject(uriComponents.toUriString(), CustomResponse.class);
    }

}
