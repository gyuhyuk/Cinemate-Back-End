package com.capstone.cinemate.Member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpResponse {
    private String memberId;
    private String nickName;

    public SignUpResponse(String memberId, String nickName) {
        this.memberId = memberId;
        this.nickName = nickName;
    }
}
