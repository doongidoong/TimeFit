package com.project.response;

import com.project.domain.Center;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
public class UserInfoResponse {
    private String email;

    private long msrl;

    private String kakao;


    private String name;

    private String phoneNumber;

    private String gender;

    private String birth;

    private Center center;

    @Builder
    public UserInfoResponse(String email, long msrl, String kakao,  String name, String phoneNumber, String gender, String birth, Center center) {
        this.email = email;
        this.msrl = msrl;
        this.kakao = kakao;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birth = birth;
        this.center = center;
    }

}

