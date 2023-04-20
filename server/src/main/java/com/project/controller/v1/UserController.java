package com.project.controller.v1;

import com.project.domain.Center;
import com.project.domain.User;
import com.project.exception.CookieNotFound;
import com.project.exception.UserExist;
import com.project.exception.UserNotFound;
import com.project.request.*;
import com.project.response.CenterSignResponse;
import com.project.response.TokenResponse;
import com.project.response.UserInfoResponse;
import com.project.service.CenterService;
import com.project.service.SignService;
import com.project.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {

    @Autowired
    private final UserInfoService userInfoService;

    /*유저 정보 가져오기 */
    @GetMapping("/user")
    public UserInfoResponse get(@AuthenticationPrincipal User user) {
        return UserInfoResponse.builder().email(user.getEmail())
                .gender(user.getGender())
                .birth(user.getBirth())
                .name(user.getName())
                .center(user.getCenter())
                .msrl(user.getMsrl())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }


    /*ID로 유저 정보 가져오기 */
    @GetMapping("/user/{msrl}")
    public UserInfoResponse get(@PathVariable Long msrl) {
        User user = userInfoService.get(msrl);
        return UserInfoResponse.builder().email(user.getEmail())
                .gender(user.getGender())
                .birth(user.getBirth())
                .name(user.getName())
                .center(user.getCenter())
                .msrl(user.getMsrl())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }


    /*유저 비밀번호 변경*/
    @Transactional
    @PostMapping("/user/change-password")
    public UserInfoResponse changePassword(@AuthenticationPrincipal User loginUser, @RequestBody UserInfoRequest userInfoRequest ) {
        return  userInfoService.changePassword(loginUser, userInfoRequest);
    }

    /*유저 센터 변경*/
    @Transactional
    @PostMapping("/user/change-center")
    public UserInfoResponse changeCenter(@AuthenticationPrincipal User loginUser, @RequestBody UserInfoRequest userInfoRequest ) {
        return userInfoService.changeCenter(loginUser, userInfoRequest);
    }

}




