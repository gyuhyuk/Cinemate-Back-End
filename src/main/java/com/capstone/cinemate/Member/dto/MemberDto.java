package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;

public record MemberDto (
    Long id,
    String memberId,
    String password,
    String nickName

) {
    public static MemberDto of(Long id, String memberId, String password, String nickName) {
        return new MemberDto(id, memberId, password, nickName);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getId(),
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
