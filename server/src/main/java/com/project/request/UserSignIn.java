package com.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UserSignIn {
    @NotBlank(message = "아이디를 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
    @Builder
    public UserSignIn(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

