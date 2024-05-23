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

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/auth/sign-up")
    public CustomResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new CustomResponse<>(
                200,
                "회원가입에 성공했습니다.",
                memberService.signUp(signUpRequest)
        );
    }

    @PostMapping("/api/auth/sign-in")
    public CustomResponse<TokenResponse> signIn(@Valid @RequestBody LoginRequest loginRequest) throws CustomException {
        return new CustomResponse<>(
                HttpStatus.OK.value(), "로그인에 성공했습니다.", memberService.signIn(loginRequest)
        );
    }

    @GetMapping("/api/memberId/{memberId}/exists")
    public CustomResponse<Boolean> checkMemberIdDuplicate(@PathVariable String memberId) {
        return new CustomResponse<> (
                200,
                "null",
                memberService.checkMemberIdDuplicate(memberId)
                );
    }

    @GetMapping("/api/nickname/{nickName}/exists")
    public CustomResponse<Boolean> checkNicknameDuplicate(@PathVariable String nickName) {
        return new CustomResponse<> (
                200,
                "null",
                memberService.checkNicknameDuplicate(nickName)
        );
    }

    @GetMapping("/api/recommendation")
    public ResponseEntity<RecommendationResponse> getMovieRecommendation(@TokenInformation @RequestParam("userId") Long memberId,
                                                                         @RequestParam(value = "genreId", required = false) Long genreId) {
        return ResponseEntity.ok().body(memberService.recommend(memberId, genreId));
    }
}
