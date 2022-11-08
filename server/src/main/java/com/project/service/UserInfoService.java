package com.project.service;


import com.project.domain.Center;
import com.project.domain.User;
import com.project.exception.CenterNotFound;
import com.project.exception.UserNotFound;
import com.project.repository.CenterImgRepository;
import com.project.repository.CenterRepository;
import com.project.repository.UserRepository;
import com.project.request.CenterSearch;
import com.project.request.UserInfoRequest;
import com.project.response.CenterDetailResponse;
import com.project.response.CenterImgResponse;
import com.project.response.CenterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j //로그 작성
@Service  //서비스 레이어
@RequiredArgsConstructor  //lombok을 통해 생성자처리
public class UserInfoService {

    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;


    public User get(Long msrl) {
        User user = userRepository.findById(msrl)
                .orElseThrow(CenterNotFound::new);
        return user;
    }

    public User changePassword(UserInfoRequest request){
        //Long msrl, String Email
        User user= userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFound::new);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return user;
    }
    public User changeCenter(UserInfoRequest request){
        //Long msrl, String Email
        User user= userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFound::new);
        Center center = centerRepository.findByIdFetchJoin(request.getCenterId());
        user.setCenter(center);
        userRepository.save(user);
        return user;
    }

}
