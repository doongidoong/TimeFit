package com.project.controller.v1;


// import 생략


import com.google.gson.Gson;
import com.project.domain.User;
import com.project.response.KakaoAuth;
import com.project.response.KakaoProfile;
import com.project.response.TokenResponse;
import com.project.service.KakaoService;
import com.project.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/social/login")
public class SocialController {


    private final Environment env;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final KakaoService kakaoService;
    private final SignService signService;

    @Value("${spring.url.base}")
    private String baseUrl;

    @Value("${spring.social.kakao.client_id}")
    private String kakaoClientId;

    @Value("${spring.social.kakao.redirect}")
    private String kakaoRedirect;



    /**
     * 카카오 로그인 페이지, rest api 형식이므로 버튼만 나오도록
     */
    @GetMapping
    public ModelAndView socialLogin(ModelAndView mav) {

//        카카오 로그인을 리퀘스트 생성
        StringBuilder loginUrl = new StringBuilder()
                .append(env.getProperty("spring.social.kakao.url.login"))
                .append("?client_id=").append(kakaoClientId)
                .append("&response_type=code")
                .append("&redirect_uri=").append(baseUrl).append(kakaoRedirect);
        mav.addObject("loginUrl", loginUrl);
        mav.setViewName("social/login");
        return mav;
    }

    /**
     * 카카오 인증 완료 후 리다이렉트 화면
     */
    @GetMapping(value = "/kakao")
    public @ResponseBody void redirectKakao(@RequestParam String code, HttpServletResponse response) throws IOException {
        KakaoAuth kakaoAuth = kakaoService.getKakaoTokenInfo(code);
        KakaoProfile profile =kakaoService.getKakaoProfile(kakaoAuth.getAccess_token());
//        String email = profile.getKakao_account().getEmail();
        Optional<User> user= kakaoService.getByKakao(profile);
//         이후 만약 empty일 경우, 회원가입으로 이동 아닐 경우 로그인 진행
        if(user.isEmpty()){
            response.sendRedirect("http://localhost:3000/join/kakao?kakaoId="+profile.getId());
        }
        else{
            TokenResponse tokenResponse = signService.signInByKakao(profile.getId());
            Cookie accessCookie = new Cookie("AccessToken", tokenResponse.getAccessToken());
            accessCookie.setPath("/");
////        accessCookie.setHttpOnly(true);
////        accessCookie.setSecure(true);
            response.addCookie(accessCookie);
            Cookie refreshCookie = new Cookie("RefreshToken", tokenResponse.getAccessToken());
            refreshCookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
            response.addCookie(refreshCookie);
            response.sendRedirect("http://localhost:3000/");
        }

    }

}