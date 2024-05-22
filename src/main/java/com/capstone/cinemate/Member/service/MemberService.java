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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.*;

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
        // 회원 닉네임 중복 검사
        if (memberRepository.findByNickName(signUpRequest.getNickName()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME_RESOURCE);
        }
        // 회원 ID 중복 검사
        if (memberRepository.findByMemberId(signUpRequest.getMemberId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL_RESOURCE);
        }
        Member member =
                memberRepository.save(
                        Member.builder()
                                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                                .memberId(signUpRequest.getMemberId())
                                .nickName(signUpRequest.getNickName())
                                .build());

        return new SignUpResponse(member.getMemberId(), member.getNickName());
    }

    @Transactional
    public TokenResponse signIn(LoginRequest loginRequest) throws CustomException {
            Member member =
                    memberRepository
                            .findByMemberId(loginRequest.getMemberId())
                            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            if(!hasSamePassword(member,loginRequest)){
                throw new CustomException(ErrorCode.WRONG_PASSWORD);
            }

            String accessToken = tokenUtils.generateJwtToken(member);

            return new TokenResponse(accessToken, member.getSurvey());
    }

    // 비밀번호 확인
    private boolean hasSamePassword(Member member, LoginRequest loginRequest) {
        return passwordEncoder.matches(loginRequest.getPassword(), member.getPassword());
    }

    // 아이디 중복 체크
    public boolean checkMemberIdDuplicate(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    // 닉네임 중복 체크
    public boolean checkNicknameDuplicate(String nickName) {
        return memberRepository.existsByNickName(nickName);
    }

    public Member findMemberByJwt(final String jwt) {
        // getClaimsFormToken 메서드를 이용하여 Claims 객체를 얻어냄
        Claims claims = tokenUtils.getClaimsFormToken(jwt);
        // Claims 객체에서 memberId를 추출
        String memberId = claims.get("memberId", String.class);

        // memberId를 이용하여 MemberRepository에서 사용자 정보 조회
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public RecommendationResponse recommend(Long memberId) {
        // 기본 추천
        List<Long> defaultRecommendIdResult = (List<Long>) recommendRequest(memberId).getData();
        for (int i = 0; i < defaultRecommendIdResult.size(); i++) {
            defaultRecommendIdResult.set(i, defaultRecommendIdResult.get(i) - 1);
        }

        // 유저 설문 결과 불러오기(장르만)
//        List<Long> genreMovieIdList1 = new ArrayList<>();
//        List<Long> genreMovieIdList2 = new ArrayList<>();
//        List<Long> genreMovieIdList3 = new ArrayList<>();

        // 장르 ID 리스트를 가져온다고 가정
        List<Long> genreIdList = genreMemberRepository.findGenreIdsByMemberId(memberId);

        List<List<Movie>> genreMovieLists = new ArrayList<>();
        for (Long idx : genreIdList) {
            List<Long> genreMovieIdList = fetchGenreRecommendations(memberId, genreIdList.get(idx.intValue()));
            genreMovieLists.add(movieRepository.findAllByIdIn(genreMovieIdList));
        }

        // 영화 ID 리스트를 영화 상세 정보로 변환
        List<Movie> defaultRecommendResult = movieRepository.findAllByIdIn(defaultRecommendIdResult);
//        List<Movie> genreMovieList1 = movieRepository.findByIds(genreMovieIdList1);
//        List<Movie> genreMovieList2 = movieRepository.findByIds(genreMovieIdList2);
//        List<Movie> genreMovieList3 = movieRepository.findByIds(genreMovieIdList3);

        // 장르별 추천 리스트를 하나의 리스트로 감쌈
//        List<List<Movie>> genreMovieLists = new ArrayList<>();
//        genreMovieLists.add(genreMovieList1);
//        genreMovieLists.add(genreMovieList2);
//        genreMovieLists.add(genreMovieList3);

        // 결과 반환
        RecommendationResponse response = new RecommendationResponse();
        response.setDefaultRecommendResult(defaultRecommendResult);
        response.setGenreMovieLists(genreMovieLists);

        return response;
//    }
//        // 기본 추천
//        List<Long> defaultRecommendIdResult = recommendRequest(memberId).data;
//        for (Long idx = 0L; idx < defaultRecommendIdResult.size(); idx++) {
//            defaultRecommendIdResult.set(defaultRecommendIdResult.get(idx) - 1);
//        }
//        // ML_SERVER_URL+"/recommendation?userId=1"
//
//
//        // 유저 설문 결과 불러오기(장르만)
//        //장르 받아
//
//        List<Long> genreMovieIdList1 = new ArrayList<>();
//        List<Long> genreMovieIdList2 = new ArrayList<>();
//        List<Long> genreMovieIdList3 = new ArrayList<>();
//        for (Long idx = 0L; idx < genre_id_list.size(); idx++) {
//
//            List<Long> result = recommendRequest(memberId).data;
//            for (int id::result) {
//                genreMovieIdList.add(id - 1);
//            }
//            // 첫번째 장르
//            // ML_SERVER_URL+"/recommendation?userId=1&genreId=3"
//            // 두번째 장르
//            // ML_SERVER_URL+"/recommendation?userId=1&genreId=3"
//            // 세번째 장르
//            // ML_SERVER_URL+"/recommendation?userId=1&genreId=3"
//        }
//
//        List<Movie> defaultRecommendResult = new ArrayList<>();
//        List<Movie> genreMovieList = new ArrayList<>();
//
//        // 위에서 받은 4개를 하나하나 까면서(id list니까) 영화 데이터 리스트로 바꾸는 작업을 마친 후 그 영화 리스트를 반환
//
//        // defaultRecommendIdResult 얘를 까
//        movieRepository.findById();
//        // genreMovieIdList 얘를 까
//        movieRepository.findById();
//
//        // return {defaultRecommendResult = defaultRecommendResult, genreMovieList = genreMovieList}
    }

    private List<Long> fetchGenreRecommendations(Long memberId, Long genreId) {
        List<Long> genreMovieIdList = (List<Long>) recommendRequest(memberId, genreId).getData();
        for (int i = 0; i < genreMovieIdList.size(); i++) {
            genreMovieIdList.set(i, genreMovieIdList.get(i) - 1);
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
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

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
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectTimeout(5000); // api 호출 타임아웃
//        factory.setReadTimeout(5000); // api 읽기 타임아웃

        RestTemplate restTemplate = new RestTemplate(factory);

        // 응답
        return restTemplate.getForObject(uriComponents.toUriString(), CustomResponse.class);
    }
}