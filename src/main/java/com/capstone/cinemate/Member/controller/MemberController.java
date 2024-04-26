package com.capstone.cinemate.Member.controller;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.LoginRequest;
import com.capstone.cinemate.Member.dto.SignUpRequest;
import com.capstone.cinemate.Member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/api/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest, Errors errors) {
        if (errors.hasErrors()) {
            // 유효성 검사 실패 시 오류 메시지 반환
            Map<String, String> validationErrors = new HashMap<>();
            errors.getFieldErrors().forEach(error ->
                    validationErrors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(validationErrors);
        }

        if (memberService.findByNickName(signUpRequest.getNickName()).isPresent()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "이미 존재하는 닉네임입니다."));
        } else if (memberService.findByMemberId(signUpRequest.getMemberId()).isPresent()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "이미 존재하는 id입니다."));
        }
        return ResponseEntity.ok(memberService.signUp(signUpRequest));
    }
    @PostMapping("/api/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginRequest, Errors errors) throws Exception {
            if (errors.hasErrors()) {
                // 유효성 검사 실패 시 구체적인 오류 메시지를 반환합니다.
                List<String> errorMessages = errors.getAllErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages); // 오류 메시지 리스트를 포함하여 반환
            }
            return ResponseEntity.ok().body(memberService.signIn(loginRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<List<Member>> findMember() {
        return ResponseEntity.ok().body(memberService.findMembers());
    }
}
