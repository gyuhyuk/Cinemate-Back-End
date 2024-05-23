package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;

public record MemberDto (
    String email,
    String password,
    String nickName,
    Boolean survey

) {
    public static MemberDto of(String email, String password, String nickName, Boolean survey) {
        return new MemberDto(email, password, nickName, survey);
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getMemberId(),
                entity.getPassword(),
                entity.getNickName(),
                entity.getSurvey()
        );
    }

    public Member toEntity() {
        return Member.of(
                email,
                password,
                nickName,
                survey
        );
    }
}
