package com.project.request;

import com.project.domain.CenterImages;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Setter
@Getter
@ToString
public class CenterSignUp {
    @NotBlank(message = "아이디를 입력해주세요")
    private String email;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "도시를 입력해주세요")
    private String region;

    @NotBlank(message = "주소를 입력해주세요")
    private String address;

    @NotBlank(message = "연락처를 입력해주세요")
    private String phoneNumber;

    @NotBlank(message = "사업장등록번호를 입력해주세요")
    private String storeNumber;

    private Integer price;
    @Builder
    public CenterSignUp(String email, Integer price, String name, String password,String region, String address, String phoneNumber, String storeNumber) {
        this.email = email;
        this.name = name;
        this.price = price;
        this.password = password;
        this.region = region;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.storeNumber = storeNumber;
    }
}
