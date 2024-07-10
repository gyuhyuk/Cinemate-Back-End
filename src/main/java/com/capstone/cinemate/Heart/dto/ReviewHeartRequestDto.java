package com.capstone.cinemate.Heart.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewHeartRequestDto {
    private Long memberId;
    private Long reviewId;

    public ReviewHeartRequestDto(Long memberId, Long reviewId) {
        this.memberId = memberId;
        this.reviewId = reviewId;
    }
}
