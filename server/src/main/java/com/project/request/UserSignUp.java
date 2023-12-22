package com.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class UserSignUp {
    @NotBlank(message = "아이디를 입력해주세요")
    private String email;

    private String name;
    private String gender;
    private String birth;


    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;


    @NotBlank(message = "연락처를 입력해주세요")
    private String phoneNumber;

    @Builder
    public UserSignUp(String email, String name,String gender, String password, String birth,  String phoneNumber ) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birth = birth;

    }
}