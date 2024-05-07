package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;

public record MemberDto (
    String memberId,
    String password,
    String nickName

) {
    public static MemberDto of(String memberId, String password, String nickName) {
        return new MemberDto(memberId, password, nickName);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getMemberId(),
                entity.getPassword(),
                entity.getNickName());
    }

    public Member toEntity() {
        return Member.of(
                memberId,
                password,
                nickName
        );
    }
}
