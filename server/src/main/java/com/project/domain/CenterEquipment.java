package com.project.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity  //데이터베이스에 사용될 entity를 정의
@Getter  //getter 자동 선언
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CenterEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="center_equipmentId")
    private Long id;

    @ManyToOne
    @JoinColumn(name="center_id")
    private Center center;

    @ManyToOne
    @JoinColumn(name="equipment_id")
    private Equipment equipment;

}
