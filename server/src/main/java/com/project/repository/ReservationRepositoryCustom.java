package com.project.repository;

import com.project.domain.Reservation;
import com.project.domain.User;
import com.project.request.ReservationRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {

    boolean check(Long id, ReservationRequest reservationRequest);

    List<Reservation> getReserve(Long id, String date, Long equipment);

    List<Reservation> getMyReserve(User user);

//    void saveReservation(Long id, ReservationRequest request);
}
