package com.capstone.cinemate.Member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpResponse {
    private String email;
    private String nickName;

    public SignUpResponse(String memberId, String nickName) {
        this.email = memberId;
        this.nickName = nickName;
    }
}
