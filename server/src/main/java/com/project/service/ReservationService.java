package com.project.service;


import com.project.domain.Center;
import com.project.domain.CenterEquipment;
import com.project.domain.Reservation;
import com.project.domain.User;
import com.project.exception.CenterNotFound;
import com.project.exception.ReservationExist;
import com.project.repository.CenterEquipmentRepository;
import com.project.repository.CenterRepository;
import com.project.repository.ReservationRepository;
import com.project.request.ReservationRequest;
import com.project.request.ReservationSearch;
import com.project.response.ReservationDetailResponse;
import com.project.response.ReservationResponse;
import com.project.response.ReservationUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Transactional
@Slf4j //로그 작성
@Service  //서비스 레이어
@RequiredArgsConstructor  //lombok을 통해 생성자처리
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CenterRepository centerRepository;
    private final CenterEquipmentRepository centerEquipmentRepository;


    // 헬스장 기구의 예약 내역 조회
    public List<ReservationResponse> getReservation(Long id, ReservationSearch request){
        List<ReservationResponse> reservationList = new ArrayList<>();
        // 받은 헬스장 기구 ID 리스트를 돌면서 예약 내역 리스트 구성
        for(Long equipment : request.getSearchIds()){
            List<ReservationDetailResponse> rv =
                    reservationRepository.getReserve(id, request.getSearchDate(),equipment).stream()
                            .map(ReservationDetailResponse::new)
                            .collect(Collectors.toList());
            reservationList.add(new ReservationResponse(equipment,rv));
        }
        return reservationList;
    }
    // 예약 요청
    public synchronized void requestReservation(Long centerId, ReservationRequest request, User user){
        //예약 있을 시 예외처리
        if (!reservationRepository.check(request.getCenterEquipmentId(), request)) {
            throw new ReservationExist();
        }

        // 예약 정보를 만들기 위해 센터 id와 기구 id로부터 정보를 불러옴
        Center center = centerRepository.findById(centerId)
                .orElseThrow(CenterNotFound::new);
        CenterEquipment ce = centerEquipmentRepository.findById(request.getCenterEquipmentId())
                .orElseThrow();
        reservationRepository.saveAndFlush(Reservation.builder()
                .center(center)
                .centerEquipment(ce)
                .start(request.getStart())
                .end(request.getEnd())
                .user(user)
                .build());
    }

    // 예약 ID를 통해 예약 취소
    public void cancelReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }
    // User 정보를 통해 예약 정보를 불러와 List로 반환
    public List<ReservationUserResponse> getMyReservation(User user){
        return reservationRepository.getMyReserve(user).stream()
                .map(ReservationUserResponse::new)
                .collect(Collectors.toList());
    }






}
