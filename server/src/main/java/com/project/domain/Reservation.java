package com.project.domain;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="center_id")
    private Center center;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="center_equipmentId")
    private CenterEquipment centerEquipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="msrl")
    private User user;

    private LocalDateTime start;
    private LocalDateTime end;

}
