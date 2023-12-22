package com.project.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.repository.ReservationRepository;
import com.project.repository.UserRepository;
import com.project.request.UserSignIn;
import com.project.request.UserSignUp;
import com.project.service.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.timefit.com",uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
class SignControllerDocTest {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SignService signService;


    @BeforeEach
    void clean(){

        userRepository.deleteAll();
    }


    @Test
    @DisplayName("로그인을 실행한다")
    void signIn() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .build();
        signService.join(user);
        Assertions.assertEquals(1, userRepository.count());
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc .perform(RestDocumentationRequestBuilders.post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("signin",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("ID"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("PASSWORD")
                ), responseFields(
                        fieldWithPath("grantType").type(JsonFieldType.STRING).description("grantType"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("인증용 토큰, 쿠키에 저장"),
                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("재발급용 토큰, 쿠키에 저장"),
                        fieldWithPath("refreshTokenExpirationTime").type(JsonFieldType.NUMBER).description("리프레시 토큰 유효 기간")
                )));

    }
    @Test
    @DisplayName("로그아웃을 실행한다")
    void signOut() throws Exception{
        //회원가입
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .build();
        signService.join(user);
        Assertions.assertEquals(1, userRepository.count());
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);
        //로그인
        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //로그아웃
        mockMvc .perform(get("/signout")
                        .cookie(result.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signout"));

    }

    @Test
    @DisplayName("회원가입을 실행한다")
    void signup() throws Exception{

        UserSignUp request = UserSignUp
                .builder()
                .email("id@naver.com")
                .birth("1997-10-06")
                .gender("남")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .build();
//        System.out.println(request);
        String json = objectMapper.writeValueAsString(request);
        mockMvc .perform(post("/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("signup",requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("ID"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("PASSWORD"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("gender").type(JsonFieldType.STRING).description("성별"),
                        fieldWithPath("birth").description("생년월일 yyyy-mm-dd"),
                        fieldWithPath("phoneNumber").description("핸드폰 번호 000-0000-000")
                )));
    }

    @Test
    @DisplayName("access token 재발급을 실행한다")
    void reissue() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .build();
        signService.join(user);
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //reissue
        mockMvc .perform(get("/reissue")
                        .cookie(result.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reissue",
                        responseFields(
                                fieldWithPath("grantType").description("grantType"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("인증용 토큰, 쿠키에 저장"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("재발급용 토큰, 쿠키에 저장"),
                                fieldWithPath("refreshTokenExpirationTime").description("리프레시 토큰 유효 기간")
                        )));

    }

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void checkEmail() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .build();
        signService.join(user);
        mockMvc .perform(RestDocumentationRequestBuilders.get("/signup/check-email?email=id@naver.com")
                        )
                .andExpect(status().isConflict())
                .andDo(print())
                .andDo(document("signup/check-email",
                        queryParameters(
                                parameterWithName("email").description("찾는 이메일")
                        )));
    }

    @Test
    @DisplayName("사업자등록자번호 확인")
    void tempCheck() throws Exception{
        mockMvc .perform(RestDocumentationRequestBuilders.get("/signup/check-storeNumber").param("number","100"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("/signup/check-storeNumber"
                        ,queryParameters(
                                parameterWithName("number").description("등록자 번호"))));

    }

    @Test
    @DisplayName("이메일 찾기")
    void findEmail() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-3333-3333")
                .build();
        signService.join(user);

        mockMvc .perform(RestDocumentationRequestBuilders.get("/signin/find-email?phoneNumber=010-3333-3333"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("/signin/find-email"
                        ,queryParameters(
                                parameterWithName("phoneNumber").description("핸드폰 번호"))));
    }

    @Test
    @DisplayName("비밀번호 찾기, 임시비밀번호 생성")
    void findPassword() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-3333-3333")
                .build();
        signService.join(user);

        mockMvc .perform(get("/signin/find-password?email=id@naver.com"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("/signin/find-password"
                ,queryParameters(
                        parameterWithName("email").description("이메일"))));

    }
}
