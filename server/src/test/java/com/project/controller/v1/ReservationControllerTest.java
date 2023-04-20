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
import com.project.service.ReservationService;
import com.project.service.SignService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationControllerTest {

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
    private ReservationService reservationService;
    @Autowired
    private SignService signService;
    @BeforeEach
    void clean(){
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        equipmentRepository.deleteAll();
        userRepository.deleteAll();
        centerRepository.deleteAll();
    }

//    @AfterAll
//    void clean2(){
//        reservationRepository.deleteAll();
//        centerEquipmentRepository.deleteAll();
//        reservationRepository.deleteAll();
//        centerEquipmentRepository.deleteAll();
//        userRepository.deleteAll();
//        centerRepository.deleteAll();
//    }
    @Test
    @DisplayName("예약 확인")
    void test1() throws Exception{
        //given
        List<Center> requestCenter = IntStream.range(0,20)
                .mapToObj(i -> Center.builder()
                        .name("센터" +i)
                        .region("서울")
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


        mockMvc.perform(get("/center/{centerId}/reserve?searchIds={1},{2}&searchDate={d}"
                        ,requestCenter.get(0).getId(),requestEquip.get(1).getId(),requestEquip.get(2).getId(),now)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @DisplayName("예약 신청")
    void test2() throws Exception{
        //given
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
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        List<Center> requestCenter = IntStream.range(0,20)
                .mapToObj(i -> Center.builder()
                        .name("센터" +i)
                        .region("서울")
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

//        List<Long> ids = new ArrayList<>();
//        ids.add(requestEquip.get(1).getId());
        ReservationRequest requestR = ReservationRequest.builder()
                .centerEquipmentId(requestEquip.get(1).getId())
                .start(LocalDateTime.parse(now+"T10:45:30"))
                .end(LocalDateTime.parse(now+"T10:55:30"))
                .build();


        mockMvc.perform(post("/center/{centerId}/reserve",requestCenter.get(1).getId())
                        .cookie(result.getResponse().getCookies())
                        .content(objectMapper.writeValueAsString(requestR))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("예약 차있을 때")
    void test3() throws Exception{
        //given
        Center requestCenter = Center.builder()
                        .name("센터")
                        .region("서울")
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


        ReservationRequest request = ReservationRequest.builder()
                .centerEquipmentId(requestEquip.getId())
                .start(LocalDateTime.parse(now+"T10:15:30"))
                .end(LocalDateTime.parse(now+"T10:20:30"))
                .build();

        mockMvc.perform(post("/center/{centerId}/reserve",requestCenter.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    @DisplayName("예약 취소")
    void test4() throws Exception{
       Center requestCenter = Center.builder()
                        .name("센터")
                        .region("서울")
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


        mockMvc.perform(delete("/center/{centerId}/reserve/{reservationId}",requestCenter.getId(),requestReserve.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("내 예약 정보 가져오기")
    void test5() throws Exception{
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
        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        Center center = Center.builder()
                        .name("센터")
                        .region("서울")
                        .price(10000).build();
        centerRepository.save(center);

        List<Equipment> equipments = IntStream.range(0,5)
                .mapToObj(i -> Equipment.builder()
                        .name("헬스 기구"+i)
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
                        .start(LocalDateTime.parse(now+"T19:15:30"))
                        .end(LocalDateTime.parse(now+"T19:25:30"))
                        .build()).collect(Collectors.toList());
        reservationRepository.saveAll(requestReserve);

        mockMvc.perform(get("/my-reserve")
                        .cookie(result.getResponse().getCookies())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


//    @Test
//    @DisplayName("예약 동시성 처리")
//    void test6() throws Exception{
//
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        UserSignUp user = UserSignUp
//                .builder()
//                .email("id@naver.com")
//                .password("1234")
//                .name("이름")
//                .phoneNumber("010-2323-3333")
//                .build();
//        signService.join(user);
//        Assertions.assertEquals(1, userRepository.count());
//        UserSignIn request = UserSignIn
//                .builder()
//                .email("id@naver.com")
//                .password("1234")
//                .build();
//        String json = objectMapper.writeValueAsString(request);
//        MvcResult result = mockMvc .perform(post("/signin")
//                        .contentType(APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        Center center = Center.builder()
//                .name("센터")
//                .region("서울")
//                .price(10000).build();
//        centerRepository.save(center);
//
//        List<Equipment> equipments = IntStream.range(0,5)
//                .mapToObj(i -> Equipment.builder()
//                        .name("헬스 기구"+i)
//                        .build()).collect(Collectors.toList());
//        equipmentRepository.saveAll(equipments);
//
//        List<CenterEquipment> requestEquip = IntStream.range(0,20)
//                .mapToObj(i -> CenterEquipment.builder()
//                        .center(center)
//                        .equipment(equipments.get(i%5))
//                        .build()).collect(Collectors.toList());
//        centerEquipmentRepository.saveAll(requestEquip);
//
//        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(CenterNotFound::new);
//        LocalDate now = LocalDate.now();
//
//        ReservationRequest requestR1 = ReservationRequest.builder()
//                .centerEquipmentId(requestEquip.get(1).getId())
//                .start(LocalDateTime.parse(now+"T10:45:30"))
//                .end(LocalDateTime.parse(now+"T10:55:30"))
//                .build();
//
//
//        for(int i=0; i < 5; i++){
//
//            executorService.execute(()->{
//                reservationService.requestReservation(center.getId(),requestR1,user1);
//            });
//        }
//
//        mockMvc.perform(get("/center/{centerId}/reserve?searchIds={1}&searchDate={d}"
//                        ,center.getId(),requestEquip.get(1).getId(),now)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//    }
}