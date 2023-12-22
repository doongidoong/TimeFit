package com.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class KakaoSignUp {
    @NotBlank(message = "아이디를 입력해주세요")
    private String email;
    private long kakaoId;
    @Builder
    public KakaoSignUp(String email, long kakaoId) {
        this.email = email;
        this.kakaoId = kakaoId;
    }
}