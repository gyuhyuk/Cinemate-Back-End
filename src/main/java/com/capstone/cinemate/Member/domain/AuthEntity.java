package com.capstone.cinemate.Member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Table(name = "auth")
@Entity
public class AuthEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

//    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public AuthEntity(Member member) { // String refreshToken
//        this.refreshToken = refreshToken;
        this.member = member;
    }

    // 추후 refresh_TOKEN 필요시 활성화
//    public void refreshUpdate(String refreshToken) {
//        this.refreshToken = refreshToken;
//    }
}