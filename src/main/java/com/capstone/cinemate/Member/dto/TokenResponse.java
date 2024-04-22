package com.capstone.cinemate.Member.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {
    private String ACCESS_TOKEN;
//    private String REFRESH_TOKEN;
}