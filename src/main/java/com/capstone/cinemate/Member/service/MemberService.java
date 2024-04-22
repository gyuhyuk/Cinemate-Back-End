package com.capstone.cinemate.Member.service;

import com.capstone.cinemate.Member.domain.AuthEntity;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.dto.MemberRequest;
import com.capstone.cinemate.Member.dto.MemberResponse;
import com.capstone.cinemate.Member.dto.TokenResponse;
import com.capstone.cinemate.Member.repository.AuthRepository;
import com.capstone.cinemate.Member.repository.MemberRepository;
import com.capstone.cinemate.common.jwt.TokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenUtils tokenUtils;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findByMemberId(String memberId) {

        return memberRepository.findByMemberId(memberId);
    }

    @Transactional
    public MemberResponse signUp(MemberRequest memberRequest) {
        Member member =
                memberRepository.save(
                        Member.builder()
                                .password(passwordEncoder.encode(memberRequest.getPassword()))
                                .memberId(memberRequest.getMemberId())
                                .nickName(memberRequest.getNickName())
                                .build());

//        String accessToken = tokenUtils.generateJwtToken(member);
//        String refreshToken = tokenUtils.saveRefreshToken(member);

//        authRepository.save(
//                AuthEntity.builder().member(member).refreshToken(refreshToken).build());

//        return TokenResponse.builder().ACCESS_TOKEN(accessToken).REFRESH_TOKEN(refreshToken).build();
        return new MemberResponse(member.getMemberId(), member.getNickName());
    }

    @Transactional
    public TokenResponse signIn(MemberRequest memberRequest) throws Exception {
        Member member =
                memberRepository
                        .findByMemberId(memberRequest.getMemberId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(memberRequest.getPassword(), member.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenUtils.generateJwtToken(member);

        // RefreshToken 관련 로직을 제거하고 AccessToken만 반환합니다.
        return TokenResponse.builder()
                .ACCESS_TOKEN(accessToken)
                .build();
    }

//    @Transactional
//    public TokenResponse signIn(MemberRequest memberRequest) throws Exception {
//        Member member =
//                memberRepository
//                        .findByMemberId(memberRequest.getMemberId())
//                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//
//        Optional<AuthEntity> authEntityOptional =
//                authRepository.findByMemberId(member.getId());
//
//        String accessToken = tokenUtils.generateJwtToken(member);
//        String refreshToken;
//
//        if (authEntityOptional.isPresent()) {
//            AuthEntity authEntity = authEntityOptional.get();
//            refreshToken = authEntity.getRefreshToken();
//            if (!tokenUtils.isValidRefreshToken(refreshToken)) {
//                refreshToken = tokenUtils.saveRefreshToken(member);
//                authEntity.refreshUpdate(refreshToken);
//            }
//        } else {
//            // 리프레시 토큰이 없는 경우 새로 생성하고 저장
//            refreshToken = tokenUtils.saveRefreshToken(member);
//            authRepository.save(AuthEntity.builder().member(member).refreshToken(refreshToken).build());
//        }
//
//        return TokenResponse.builder().ACCESS_TOKEN(accessToken).REFRESH_TOKEN(refreshToken).build();
//    }


    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
}