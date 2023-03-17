package com.project.service;

import com.project.domain.Center;
import com.project.domain.CenterEquipment;
import com.project.domain.Reservation;
import com.project.domain.User;
import com.project.exception.ReservationExist;
import com.project.repository.CenterEquipmentRepository;
import com.project.repository.CenterRepository;
import com.project.repository.ReservationRepository;
import com.project.repository.UserRepository;
import com.project.request.ReservationRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private CenterEquipmentRepository centerEquipmentRepository;

    @BeforeEach
    void clean(){
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        centerRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void doubleBookingTest() throws Exception{
        //given
        User user= User.builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .phoneNumber("010-2323-3333")
                .birth("birth")
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        userRepository.save(user);
        Center center = Center.builder()
                .name("상품")
                .build();
        centerRepository.save(center);
        CenterEquipment centerEquipment = CenterEquipment.builder()
                .center(center)
                .build();
        centerEquipmentRepository.save(centerEquipment);

        ExecutorService service = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        LocalDate now = LocalDate.now();
        ReservationRequest request = ReservationRequest.builder()
                        .centerEquipmentId(centerEquipment.getId())
                        .start(LocalDateTime.parse(now+"T10:45:00"))
                        .end(LocalDateTime.parse(now+"T10:55:00"))
                        .build();
        //when
        for (int i = 0; i < 2; i++) {
            service.execute(()->{
                try{
                    reservationService.requestReservation(center.getId(), request, user);
                } catch (RuntimeException e){
                    Assertions.assertEquals("예약 불가한 시간입니다."
                            , e.getMessage());
                    log.info("예약에 실패하였습니다.");
                }
                latch.countDown();
            });
        }latch.await();

        //then
        Assertions.assertEquals(1,reservationRepository.findAll().size());

    }
}