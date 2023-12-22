package com.project.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.BatchSize;


import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Builder
@Entity  //데이터베이스에 사용될 entity를 정의
@Getter  //getter 자동 선언
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)  //인자없는 생성자를 자동 생성
@Table(name="center")
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //pk 설정을 DB에게 위임,
    @Column(name = "center_id")
    private Long id;

    private String name;
    private String region;
    private String address;
    private Integer price;
    private String phoneNumber;

    private String storeNumber;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

//    @JsonManagedReference
    @BatchSize(size = 100)
    @OneToMany(mappedBy ="center", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<CenterImages> centerImages;


//    @JsonManagedReference
    @BatchSize(size = 100)
    @OneToMany(mappedBy ="center", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Trainer> trainers;

    public CenterEditor.CenterEditorBuilder toEditor(){
        return CenterEditor
                .builder()
                .phoneNumber(phoneNumber)
                .price(price)
                .address(address)
                .region(region)
                .name(name);
    }

    public void edit(CenterEditor centerEditor){
        price = centerEditor.getPrice();
        region = centerEditor.getRegion();
        address = centerEditor.getAddress();
        name =centerEditor.getName();
        phoneNumber = centerEditor.getPhoneNumber();
    }
}