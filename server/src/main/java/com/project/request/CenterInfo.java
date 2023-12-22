package com.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class CenterInfo {

    private long centerId;
    private String name;

    private String region;

    private String address;

    private String phoneNumber;

    private Integer price;

    @Builder
    public CenterInfo(long centerId, Integer price, String name, String region, String address, String phoneNumber) {
        this.centerId = centerId;
        this.price = price;
        this.name = name;
        this.region = region;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}