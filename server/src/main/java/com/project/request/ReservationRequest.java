package com.project.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@Getter
public class ReservationRequest {

    private final Long centerEquipmentId;
    private final LocalDateTime start;
    private final LocalDateTime end;

}
