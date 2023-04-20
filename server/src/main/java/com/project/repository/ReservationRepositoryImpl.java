package com.project.repository;

import com.project.domain.*;
import com.project.request.ReservationRequest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.domain.QReservation.reservation;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;
    //예약 가능한지 체크, 원하는 예약 시간 사이에 start or end 값이 존재하지 않으면 true 리턴
    @Override
    public boolean check(Long id, ReservationRequest request){
        if (jpaQueryFactory.selectFrom(reservation)
                .where(reservation.center.id.eq(id),
                        reservation.centerEquipment.id.eq(request.getCenterEquipmentId()),
                        reservation.start.between(request.getStart(),request.getEnd())
                                .or(reservation.end.between(request.getStart(),request.getStart()))                        )
                .fetch().size() == 0)
            return true;
        return false; 
    }

    // 예약정보 확인
    @Override
    public List<Reservation> getReserve(Long id, String date, Long equipment){

        LocalDate now = LocalDate.now();
        if(date != null){
            now = LocalDate.parse(date); // 날짜정보가 없으면 당일 날로 설정
        }
        // 해당 날짜에 0~24시까지의 내역을 조회
        LocalDateTime st = LocalDateTime.parse(now+"T00:00:00");
        LocalDateTime end = LocalDateTime.parse(now+"T23:59:59");
        return jpaQueryFactory.selectFrom(reservation)
                .where(reservation.center.id.eq(id),
                        reservation.centerEquipment.id.eq(equipment),
                        reservation.start.between(st,end))
                .orderBy(reservation.start.asc())
                .fetch();
    }
    // User 정보를 바탕으로 예약 시간이 아직 도달하지 않은 내역 조회
    @Override
    public List<Reservation> getMyReserve(User user){
        LocalDateTime now = LocalDateTime.now();
        return jpaQueryFactory.selectFrom(reservation)
                .where(reservation.user.eq(user),
                        reservation.start.goe(LocalDateTime.from(now)))
                .orderBy(reservation.start.asc())
                .fetch();
    }


}
