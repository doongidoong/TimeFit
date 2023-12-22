package com.project.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.Center;
import com.project.domain.CenterEquipment;
import com.project.domain.CenterImages;
import com.project.domain.Equipment;
import com.project.repository.*;
import com.project.request.*;
import com.project.service.EquipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.service.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.persistence.EntityManager;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class EquipmentControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CenterEquipmentRepository centerEquipmentRepository;


    @Autowired
    private CenterImgRepository centerImgRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private CenterRepository centerRepository;

    EntityManager em;

    @Autowired
    private SignService signService;

    @BeforeEach
    void clean(){
        reservationRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        equipmentRepository.deleteAll();
        userRepository.deleteAll();
        centerRepository.deleteAll();
//        UserSignUp user = UserSignUp
//                .builder()
//                .email("id@naver.com")
//                .password("1234")
//                .name("이름")
//                .phoneNumber("010-2323-3333")
//                .build();
//        signService.join(user);
    }
    @Test
    void getEquipments() throws Exception {
        mockMvc.perform(get("/equipment")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void addEquipmentCategory() throws Exception{
        EquipmentCategory equipmentCategory
                = EquipmentCategory.builder()
                .name("name")
                .img("img")
                .build();
        String json = objectMapper.writeValueAsString(equipmentCategory);
        mockMvc .perform(post("/equipment/add")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        Assertions.assertEquals(1, equipmentRepository.count());
        equipmentRepository.deleteAll();
    }

    @Test
    void addCenterEquipment() throws Exception {
        Center center= Center.builder()
                .name("이름")
                .region("지역")
                .address("주소")
//                .storeNumber(request.getStoreNumber())
                .build();

        Equipment equipment = Equipment.builder()
                .name("ss")
                .img("sss")
                .build();

        centerRepository.save(center);
        equipmentRepository.save(equipment);

        CenterEquipmentAdd centerEquipmentAdd =
                CenterEquipmentAdd.builder().equipment(equipment.getId()).center(center.getId()).xLoc(30L).yLoc(20L).build();

        String json = objectMapper.writeValueAsString(centerEquipmentAdd);

        mockMvc.perform(post("/equipment/add-center")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk());
        Assertions.assertEquals(1, centerEquipmentRepository.count());
    }

    @Test
    void getCenterEquipments() throws Exception {
        Center center= Center.builder()
                .name("이름")
                .region("지역")
                .address("주소")
//                .storeNumber(request.getStoreNumber())
                .build();
        Equipment equipment = Equipment.builder()
                .name("ss")
                .img("sss")
                .build();
        centerRepository.save(center);
        equipmentRepository.save(equipment);
        CenterEquipment centerEquipment =
                CenterEquipment.builder().equipment(equipment).center(center).xLoc(30L).yLoc(20L).build();
        centerEquipmentRepository.save(centerEquipment);

        mockMvc.perform(get("/equipment/{centerId}",center.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        centerEquipmentRepository.deleteAll();
        equipmentRepository.deleteAll();
        centerRepository.deleteAll();

    }
}