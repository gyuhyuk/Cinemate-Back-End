package com.capstone.cinemate.Member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {
    private String memberId;
    private String nickName;

    public MemberResponse(String memberId, String nickName) {
        this.memberId = memberId;
        this.nickName = nickName;
    }
}
