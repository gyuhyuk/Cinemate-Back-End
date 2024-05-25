package com.capstone.cinemate.Member.dto;

import com.capstone.cinemate.Member.domain.Member;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{6,14}", message = "비밀번호는 6~14자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
}