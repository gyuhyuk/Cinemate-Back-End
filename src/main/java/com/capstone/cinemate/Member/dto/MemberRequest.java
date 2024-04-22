package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String memberId;
    private String password;
    private String nickName;

    public MemberRequest() {
        // 기본 생성자가 필요할 수 있으므로 추가
    }

    public MemberRequest(Member member){
        this.memberId = member.getMemberId();
        this.password = member.getPassword();
        this.nickName = member.getNickName();
    }
}