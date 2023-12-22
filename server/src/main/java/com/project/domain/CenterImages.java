package com.project.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Entity  //데이터베이스에 사용될 entity를 정의
@Getter  //getter 자동 선언
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)  //인자없는 생성자를 자동 생성
public class CenterImages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long id;
    private String originFileName;
    private String newFileName;
    private String filePath;

//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "center_id")
    private Center center;
}
