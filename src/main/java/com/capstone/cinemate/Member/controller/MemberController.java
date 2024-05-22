package com.capstone.cinemate.Member.controller;

import com.capstone.cinemate.Member.dto.LoginRequest;
import com.capstone.cinemate.Member.dto.SignUpRequest;
import com.capstone.cinemate.Member.dto.SignUpResponse;
import com.capstone.cinemate.Member.dto.TokenResponse;
import com.capstone.cinemate.Member.service.MemberService;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.response.CustomResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/sign-up")
    public CustomResponse<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return new CustomResponse<>(
                200,
                "회원가입에 성공했습니다.",
                memberService.signUp(signUpRequest)
        );
    }

    @PostMapping("/api/sign-in")
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

//    @GetMapping("/api/recommendation")
//    public ResponseEntity<List<Member>> findMember() {
//        return ResponseEntity.ok().body(memberService.findMembers());
//    }
}
