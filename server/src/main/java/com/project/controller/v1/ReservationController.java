package com.project.controller.v1;

import com.project.domain.User;
import com.project.request.ReservationRequest;
import com.project.request.ReservationSearch;
import com.project.response.ReservationResponse;
import com.project.response.ReservationUserResponse;
import com.project.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController // 결과값을 JSON으로 출력
@RequiredArgsConstructor ////lombok을 통해 생성자처리
public class ReservationController {

        private final ReservationService reservationService;
        @PostMapping("/center/{centerId}/reserve")
        public void requestReserve(@PathVariable Long centerId, @RequestBody ReservationRequest request,
                                   @AuthenticationPrincipal User user){
            reservationService.requestReservation(centerId, request, user);
        }

        @GetMapping("/center/{centerId}/reserve")
        public List<ReservationResponse> getReservation(@PathVariable Long centerId,
                                                        @ModelAttribute ReservationSearch request){
            return reservationService.getReservation(centerId,request);
        }

        @DeleteMapping("/center/{centerId}/reserve/{reservationId}")
        public void cancelReservation(@PathVariable Long reservationId){
            reservationService.cancelReservation(reservationId);
        }

        @GetMapping("/my-reserve")
        public List<ReservationUserResponse> getMyReservation(@AuthenticationPrincipal User user){
            return reservationService.getMyReservation(user);
        }

        @GetMapping("/lock-test/{lockId}")
        public Long lockTest(@PathVariable long lockId){
            return reservationService.lockTest(lockId);
        }
}
