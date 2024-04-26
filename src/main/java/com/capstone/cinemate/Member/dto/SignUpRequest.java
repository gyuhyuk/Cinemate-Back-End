package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "이메일 형식에 맞지 않습니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 16)
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$" , message = "닉네임은 특수문자를 포함하지 않은 2~10자리여야 합니다.")
    private String nickName;

    public SignUpRequest() {
        // 기본 생성자가 필요할 수 있으므로 추가
    }

    public SignUpRequest(Member member){
        this.memberId = member.getMemberId();
        this.password = member.getPassword();
        this.nickName = member.getNickName();
    }
}