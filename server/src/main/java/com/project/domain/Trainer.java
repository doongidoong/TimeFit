package com.project.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import jakarta.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;

//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name="center_id")
    private Center center;
}
