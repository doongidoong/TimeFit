package com.project.request;

import com.project.domain.Center;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserInfoRequest {
    private String email;

    private String password;

    private Long centerId;

    @Builder
    public UserInfoRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

