package com.capstone.cinemate.Member.controller;

import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.Member.dto.*;
import com.capstone.cinemate.Member.service.MemberService;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입
    @PostMapping("/api/auth/sign-up")
    public CustomResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new CustomResponse<>(
                200,
                "회원가입에 성공했습니다.",
                memberService.signUp(signUpRequest)
        );
    }

    // 로그인
    @PostMapping("/api/auth/sign-in")
    public CustomResponse<TokenResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) throws CustomException {
        return new CustomResponse<>(
                HttpStatus.OK.value(), "로그인에 성공했습니다.", memberService.signIn(loginRequest)
        );
    }

    // 이메일 중복 확인
    @GetMapping("/api/email/{memberId}/exists")
    public CustomResponse<Boolean> checkMemberIdDuplicate(@PathVariable String memberId) {
        return new CustomResponse<> (
                HttpStatus.OK.value(), "null",
                memberService.checkMemberIdDuplicate(memberId)
                );
    }

    // 닉네임 중복 확인
    @GetMapping("/api/nickname/{nickName}/exists")
    public CustomResponse<Boolean> checkNicknameDuplicate(@PathVariable String nickName) {
        return new CustomResponse<> (
                HttpStatus.OK.value(),
                "null",
                memberService.checkNicknameDuplicate(nickName)
        );
    }

    // 영화 추천
    @GetMapping("/api/recommendation")
    public CustomResponse<RecommendationResponse> getMovieRecommendation(@TokenInformation Long memberId) {
        return new CustomResponse<>(
                HttpStatus.OK.value(), "Success", memberService.recommend(memberId));
    }

    @GetMapping("/api/mypage")
    public CustomResponse<?> getMyPage(@TokenInformation Long memberId) {
        return new CustomResponse<>(HttpStatus.OK.value(), "Success", memberService.getMyPage(memberId));
    }
}
