package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;

public record MemberReviewDto (
        String nickName

) {
    public static MemberReviewDto of(String nickName) {
        return new MemberReviewDto(nickName);
    }

    public static MemberReviewDto from(Member entity) {
        return new MemberReviewDto(
                entity.getNickName()
        );
    }
}
