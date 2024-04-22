package com.capstone.cinemate.Member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) private String memberId;

    private String password;

    @Column(unique = true) private String nickName;

    @Builder
    public Member(String memberId, String password, String nickName) {
        this.memberId = memberId;
        this.password = password;
        this.nickName = nickName;
    }
}