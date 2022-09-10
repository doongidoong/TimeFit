package com.project.repository;

import com.project.domain.*;
import com.project.request.ReservationRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.domain.QCenter.center;
import static com.project.domain.QCenterEquipment.centerEquipment;
import static com.project.domain.QReservation.reservation;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean check(Long id, ReservationRequest request){
        if (jpaQueryFactory.selectFrom(reservation)
                .where(reservation.center.id.eq(id),
                        reservation.equipment.id.eq(request.getEquipmentId()),
                        reservation.start.between(request.getStart(),request.getEnd()),
                        reservation.end.between(request.getStart(),request.getStart()))
                .fetch() == null)
            return true;
        return false;
    }
    @Override
    public List<Reservation> getReserve(Long id, ReservationRequest request){
        LocalDate now = LocalDate.now();
        LocalDateTime st = LocalDateTime.parse(now+"T00:00:00");
        LocalDateTime end = LocalDateTime.parse(now+"T23:59:59");
        return jpaQueryFactory.selectFrom(reservation)
                .where(reservation.center.id.eq(id),
                        reservation.equipment.id.eq(request.getEquipmentId()),
                        reservation.start.between(st,end))
                .fetch();
    }

//    @Override
//    @Transactional
//    public void saveReservation(Long id, ReservationRequest request){
//        Center ct = jpaQueryFactory.selectFrom(center)
//                .where(center.id.eq(id)).fetchOne();
//        CenterEquipment eq = jpaQueryFactory.selectFrom(centerEquipment)
//                .where(centerEquipment.id.eq(request.getEquipmentId())).fetchOne();
//        Reservation rv = Reservation.builder()
//                .center(ct)
//                .equipment(eq)
//                .start(request.getStart())
//                .end(request.getEnd())
//                .build();
////        reservationRepository.save(rv);
//    }
}
