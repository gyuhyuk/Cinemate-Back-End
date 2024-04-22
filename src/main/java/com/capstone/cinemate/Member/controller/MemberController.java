package com.capstone.cinemate.Member.controller;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.MemberRequest;
import com.capstone.cinemate.Member.dto.MemberResponse;
import com.capstone.cinemate.Member.dto.TokenResponse;
import com.capstone.cinemate.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/sign-up")
    public ResponseEntity<MemberResponse> signUp(@RequestBody MemberRequest memberRequest) {
        return memberService.findByMemberId(memberRequest.getMemberId()).isPresent()
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.ok(memberService.signUp(memberRequest));
    }

    @PostMapping("/api/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody MemberRequest memberRequest) throws Exception {

        return ResponseEntity.ok().body(memberService.signIn(memberRequest));
    }

    @GetMapping("/info")
    public ResponseEntity<List<Member>> findMember() {
        return ResponseEntity.ok().body(memberService.findMembers());
    }
}