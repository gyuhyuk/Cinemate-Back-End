package com.capstone.cinemate.Member.service;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.LoginRequest;
import com.capstone.cinemate.Member.dto.SignUpRequest;
import com.capstone.cinemate.Member.dto.SignUpResponse;
import com.capstone.cinemate.Member.dto.TokenResponse;
import com.capstone.cinemate.Member.repository.AuthRepository;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.common.exception.CustomException;
import com.capstone.cinemate.common.exception.ErrorCode;
import com.capstone.cinemate.common.jwt.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenUtils tokenUtils;
    private final PasswordEncoder passwordEncoder;

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

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
}