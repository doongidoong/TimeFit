package com.project.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.User;
import com.project.repository.UserRepository;
import com.project.request.UserSignIn;
import com.project.request.UserSignUp;
import com.project.service.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
class SignControllerTest {


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
        assertEquals(1, userRepository.count());
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
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
        assertEquals(1, userRepository.count());
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
                .andDo(print());


    }

    @Test
    @DisplayName("회원가입을 실행한다")
    void signup() throws Exception{
        UserSignUp request = UserSignUp
                .builder()
                .email("id@naver.com")
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
                .andDo(print());
        assertEquals(1, userRepository.count());
        User user = userRepository.findAll().get(0);
        assertEquals("id@naver.com",user.getEmail());
        assertEquals("이름",user.getName());
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
                .andDo(print());

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
        mockMvc .perform(get("/signup/check-email?email=id@naver.com"))
                .andExpect(status().isConflict())
                .andDo(print());

    }

    @Test
    @DisplayName("사업자등록자번호 확인")
    void tempCheck() throws Exception{
        mockMvc .perform(get("/signup/check-storeNumber/").param("number","100"))
                .andExpect(status().isNotFound())
                .andDo(print());
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

        mockMvc .perform(get("/signin/find-email?phoneNumber=010-3333-3333"))
                .andExpect(status().isOk())
                .andExpect(content().string("id@naver.com"))
                .andDo(print());
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
                .andDo(print());
    }
}
