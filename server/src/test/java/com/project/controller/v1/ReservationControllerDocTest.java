package com.project.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.*;
import com.project.exception.CenterNotFound;
import com.project.exception.ReservationExist;
import com.project.repository.*;
import com.project.request.ReservationRequest;
import com.project.request.ReservationSearch;
import com.project.request.UserSignIn;
import com.project.request.UserSignUp;
import com.project.service.SignService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.timefit.com",uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationControllerDocTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private CenterEquipmentRepository centerEquipmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private SignService signService;
    @BeforeEach
    void clean(){
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        userRepository.deleteAll();
        centerRepository.deleteAll();
    }
    @AfterAll
    void clean2(){
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        userRepository.deleteAll();
        centerRepository.deleteAll();
    }


    @Test
    @DisplayName("?????? ??????")
    void test1() throws Exception{
        //given
        List<Center> requestCenter = IntStream.range(0,20)
                .mapToObj(i -> Center.builder()
                        .name("??????" +i)
                        .region("??????")
                        .price(i*10000)
                        .build()).collect(Collectors.toList());
        centerRepository.saveAll(requestCenter);

        List<CenterEquipment> requestEquip = IntStream.range(0,20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(requestCenter.get(i%5))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        List<User> users = IntStream.range(0,20)
                .mapToObj(i -> User.builder()
                        .email(i+"id@naver.com")
                        .password("1234")
                        .name("name"+i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        LocalDate now = LocalDate.now();
        List<Reservation> requestReserve = IntStream.range(0,20)
                .mapToObj(i -> Reservation.builder()
                        .center(requestCenter.get(0))
                        .centerEquipment(requestEquip.get(i%5))
                        .user(users.get(i))
                        .start(LocalDateTime.parse(now+"T10:15:"+(10+i)))
                        .end(LocalDateTime.parse(now+"T10:25:"+(10+i)))
                        .build()).collect(Collectors.toList());
        reservationRepository.saveAll(requestReserve);


        this.mockMvc.perform(get("/center/{centerId}/reserve?searchDate={d}&searchIds={1},{2}"
                        ,requestCenter.get(0).getId(),now,requestEquip.get(1).getId(),requestEquip.get(2).getId())
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/list"
                        , pathParameters(
                                parameterWithName("centerId").description("????????? ID"),
                                parameterWithName("searchIds").description("?????? ?????? ????????? ????????? ????????? ?????? ID ?????????").optional()
                                        .attributes(key("constraint").value("?????? ID ?????? ??????")),
                                parameterWithName("searchDate").description("?????? ?????? ??????, ???????????? ?????? ?????? ?????? ??????").optional()
                                        .attributes(key("constraint").value("YYYY-MM-DD"))
                        )
                        , responseFields(
                                fieldWithPath("[].id").description("????????? ?????? ID"),
                                fieldWithPath("[].times").description("?????? ?????? ?????????"),
                                fieldWithPath("[].times[].reservationId").description("?????? ID"),
                                fieldWithPath("[].times[].userName").description("?????? ?????? ??????"),
                                fieldWithPath("[].times[].start").description("?????? ?????? ??????"),
                                fieldWithPath("[].times[].end").description("?????? ?????? ??????")
                        )
                ));

    }


    @Test
    @DisplayName("?????? ??????")
    void test2() throws Exception{
        //given
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("??????")
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
        //?????????
        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        List<Center> requestCenter = IntStream.range(0,20)
                .mapToObj(i -> Center.builder()
                        .name("??????" +i)
                        .region("??????")
                        .price(i*10000)
                        .build()).collect(Collectors.toList());
        centerRepository.saveAll(requestCenter);

        List<CenterEquipment> requestEquip = IntStream.range(0,20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(requestCenter.get(i%5))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        LocalDate now = LocalDate.now();
        List<Reservation> requestReserve = IntStream.range(0,20)
                .mapToObj(i -> Reservation.builder()
                        .center(requestCenter.get(i%5))
                        .centerEquipment(requestEquip.get(i%5))
                        .start(LocalDateTime.parse(now+"T10:15:30"))
                        .end(LocalDateTime.parse(now+"T10:25:30"))
                        .build()).collect(Collectors.toList());
        reservationRepository.saveAll(requestReserve);

        ReservationRequest request1 = ReservationRequest.builder()
                .centerEquipmentId(requestEquip.get(1).getId())
                .start(LocalDateTime.parse(now+"T10:45:30"))
                .end(LocalDateTime.parse(now+"T10:55:30"))
                .build();


        this.mockMvc.perform(post("/center/{centerId}/reserve",requestCenter.get(1).getId())
                        .contentType(APPLICATION_JSON)
                        .cookie(result.getResponse().getCookies())
                        .content(objectMapper.writeValueAsString(request1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/request"
                        , pathParameters(
                                parameterWithName("centerId").description("????????? ID"))
                        , requestFields(
                                fieldWithPath("centerEquipmentId").description("??????????????? ?????? ID"),
                                fieldWithPath("start").description("?????? ?????? ??????")
                                        .attributes(key("constraint").value("YYYY-MM-DDTHH:MM:SS")),
                                fieldWithPath("end").description("?????? ?????? ??????")
                                        .attributes(key("constraint").value("YYYY-MM-DDTHH:MM:SS"))
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ???")
    void test3() throws Exception {
        //given
        List<Center> requestCenter = IntStream.range(0, 20)
                .mapToObj(i -> Center.builder()
                        .name("??????" + i)
                        .region("??????")
                        .price(i * 10000)
                        .build()).collect(Collectors.toList());
        centerRepository.saveAll(requestCenter);

        List<CenterEquipment> requestEquip = IntStream.range(0, 20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(requestCenter.get(i % 5))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        LocalDate now = LocalDate.now();
        List<Reservation> requestReserve = IntStream.range(0, 20)
                .mapToObj(i -> Reservation.builder()
                        .center(requestCenter.get(i % 5))
                        .centerEquipment(requestEquip.get(i % 5))
                        .start(LocalDateTime.parse(now + "T10:15:30"))
                        .end(LocalDateTime.parse(now + "T10:25:30"))
                        .build()).collect(Collectors.toList());
        reservationRepository.saveAll(requestReserve);

        List<Long> ids = new ArrayList<>();
        ids.add(requestEquip.get(1).getId());
        ReservationRequest request = ReservationRequest.builder()
                .centerEquipmentId(requestEquip.get(1).getId())
                .start(LocalDateTime.parse(now + "T10:15:30"))
                .end(LocalDateTime.parse(now + "T10:20:30"))
                .build();

        this.mockMvc.perform(post("/center/{centerId}/reserve",requestCenter.get(1).getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print())
                .andDo(document("reservation/request_fail",
                        pathParameters(
                            parameterWithName("centerId").description("????????? ID")),
                        requestFields(
                            fieldWithPath("centerEquipmentId").description("??????????????? ?????? ID"),
                            fieldWithPath("start").description("?????? ?????? ??????"),
                            fieldWithPath("end").description("?????? ?????? ??????")),
                        responseFields(
                            fieldWithPath("code").description("?????? ??????"),
                            fieldWithPath("message").description("?????? ?????????"),
                            fieldWithPath("validation").description("validation"))
                                    ));
    }
    @Test
    @DisplayName("?????? ??????")
    void test4() throws Exception{
        Center requestCenter = Center.builder()
                .name("??????")
                .region("??????")
                .price(10000)
                .build();
        centerRepository.save(requestCenter);

        CenterEquipment requestEquip = CenterEquipment.builder()
                .center(requestCenter)
                .build();
        centerEquipmentRepository.save(requestEquip);

        LocalDate now = LocalDate.now();
        Reservation requestReserve = Reservation.builder()
                .center(requestCenter)
                .centerEquipment(requestEquip)
                .start(LocalDateTime.parse(now+"T10:15:30"))
                .end(LocalDateTime.parse(now+"T10:25:30"))
                .build();
        reservationRepository.save(requestReserve);

        this.mockMvc.perform(delete("/center/{centerId}/reserve/{reservationId}",requestCenter.getId(),requestReserve.getId())
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/cancel"
                        , pathParameters(
                                parameterWithName("centerId").description("????????? ID"),
                                parameterWithName("reservationId").description("?????? ID"))
                ));
    }

    @Test
    @DisplayName("??? ?????? ?????? ??????")
    void test5() throws Exception{
        //given
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("??????")
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
        //?????????
        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        Center center = Center.builder()
                .name("??????")
                .region("??????")
                .price(10000).build();
        centerRepository.save(center);

        List<Equipment> equipments = IntStream.range(0,5)
                .mapToObj(i -> Equipment.builder()
                        .name("?????? ??????"+i)
                        .build()).collect(Collectors.toList());
        equipmentRepository.saveAll(equipments);

        List<CenterEquipment> requestEquip = IntStream.range(0,20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(center)
                        .equipment(equipments.get(i%5))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(CenterNotFound::new);
        LocalDate now = LocalDate.now();
        List<Reservation> requestReserve = IntStream.range(0,5)
                .mapToObj(i -> Reservation.builder()
                        .center(center)
                        .centerEquipment(requestEquip.get(i%5))
                        .user(user1)
                        .start(LocalDateTime.parse(now+"T23:48:30"))
                        .end(LocalDateTime.parse(now+"T23:58:30"))
                        .build()).collect(Collectors.toList());
        reservationRepository.saveAll(requestReserve);

        this.mockMvc.perform(get("/my-reserve")
                        .contentType(APPLICATION_JSON)
                        .cookie(result.getResponse().getCookies())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/myReservation"
                        , responseFields(
                                fieldWithPath("[].reservationId").description("?????? ID"),
                                fieldWithPath("[].centerEquipId").description("??????????????? ?????? ID"),
                                fieldWithPath("[].equipName").description("?????? ??????"),
                                fieldWithPath("[].start").description("?????? ?????? ??????"),
                                fieldWithPath("[].end").description("?????? ?????? ??????")
                        )
                ));
    }


}
