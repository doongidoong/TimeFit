package com.project.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.domain.*;
import com.project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.timefit.com",uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class CenterControllerDocTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private CenterImgRepository centerImgRepository;
    @Autowired
    private CenterEquipmentRepository centerEquipmentRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void clean(){
        centerImgRepository.deleteAll();
        userRepository.deleteAll();
        centerEquipmentRepository.deleteAll();
        equipmentRepository.deleteAll();
        trainerRepository.deleteAll();
        centerRepository.deleteAll();
    }



//    @Test
//    @DisplayName("?????? ??????")
//    void test1() throws Exception{
//        List<Center> requestCenter = IntStream.range(0,20)
//                .mapToObj(i -> Center.builder()
//                        .name("??????" +i)
//                        .region("??????")
//                        .address("????????? ?????????")
//                        .price(i*10000)
//                        .build()).collect(Collectors.toList());
//        centerRepository.saveAll(requestCenter);
//
//        this.mockMvc.perform(get("/centers")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("search-center"
////                        , pathParameters(RequestDocumentation.parameterWithName("").description("1"))
//                        , responseFields(
//                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("?????? ID"),
//                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("?????? ??????"),
//                                fieldWithPath("[].address").type(JsonFieldType.STRING).description("?????? ??????")
//                        )
//                ));
//    }


    @Test
    @DisplayName("?????? ?????? ???????????? ????????? ?????? ??????&?????????")
    void test2() throws Exception {
        //given
        List<Center> requestCenter = IntStream.range(0, 20)
                .mapToObj(i -> Center.builder()
                        .name("??????" + i)
                        .region("??????")
                        .address("????????? ?????????")
                        .price(i * 10000)
                        .build()).collect(Collectors.toList());
        centerRepository.saveAll(requestCenter);

        List<Equipment> equipments = IntStream.range(0, 5)
                .mapToObj(i -> Equipment.builder()
                        .name("??????" + i)
                        .build()).collect(Collectors.toList());
        equipmentRepository.saveAll(equipments);

        List<CenterEquipment> requestEquip = IntStream.range(0, 20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(requestCenter.get(i % 5))
                        .equipment(equipments.get(i % 3))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        List<CenterImages> images =  IntStream.range(0, 20)
                .mapToObj(i -> CenterImages.builder()
                        .center(requestCenter.get(i))
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("??????/"+i+"/img.png")
                        .build()).collect(Collectors.toList());
        centerImgRepository.saveAll(images);
        //expected
        this.mockMvc.perform(get("/centers?name=??????&region=??????&minPrice=10000&maxPrice=20000&equipmentId=1&minNumber=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("center/search"
                        , pathParameters(
                                parameterWithName("name").description("????????? ??????").optional()
                                        .attributes(key("constraint").value("????????? ????????? ????????? ?????? ????????? ?????????. '??????'??????????????????.")),
                                parameterWithName("region").description("??????").optional()
                                        .attributes(key("constraint").value("'??????'??????????????????.")),
                                parameterWithName("minPrice").description("?????? ??????").optional(),
                                parameterWithName("maxPrice").description("?????? ??????").optional(),
                                parameterWithName("equipmentId").description("?????? ?????? ID").optional(),
                                parameterWithName("minNumber").description("?????? ??????").optional()
                                        .attributes(key("constraint").value("???????????? ???????????? ????????? ????????? ?????????"))
                        )
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("?????? ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].address").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("[].images").description("?????? ????????? ??????"),
                                fieldWithPath("[].images[].local").description("????????? ??????"),
                                fieldWithPath("[].images[].path").description("????????? ??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????????")
    void test3() throws Exception {
        //given
        Center center = Center.builder()
                .name("??????")
                .region("??????")
                .address("????????? ????????? ?????????")
                .price(50000)
                .phoneNumber("010-1234-5678")
                .build();
        centerRepository.save(center);

        List<Equipment> equipments = IntStream.range(0,5)
                .mapToObj(i -> Equipment.builder()
                        .name("??????"+i)
                        .build()).collect(Collectors.toList());
        equipmentRepository.saveAll(equipments);

        List<CenterEquipment> requestEquip = IntStream.range(0,20)
                .mapToObj(i -> CenterEquipment.builder()
                        .center(center)
                        .equipment(equipments.get(i%3))
                        .build()).collect(Collectors.toList());
        centerEquipmentRepository.saveAll(requestEquip);

        List<Trainer> trainers = IntStream.range(1,4)
                .mapToObj(i -> Trainer.builder()
                        .center(center)
                        .name("?????????"+i)
                        .gender("??????")
                        .build()).collect(Collectors.toList());
        trainerRepository.saveAll(trainers);

        List<CenterImages> images =  IntStream.range(0,3)
                .mapToObj(i -> CenterImages.builder()
                        .center(center)
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("??????/"+i)
                        .build()).collect(Collectors.toList());
        centerImgRepository.saveAll(images);
        //expected
        mockMvc.perform(get("/centers/{centerId}",center.getId())
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("center/detail"
                        , pathParameters(
                                parameterWithName("centerId").description("?????? ID")
                        )
                        , responseFields(
                                fieldWithPath("id").description("?????? ID"),
                                fieldWithPath("name").description("?????? ??????"),
                                fieldWithPath("phoneNumber").description("?????? ????????????"),
                                fieldWithPath("address").description("?????? ??????"),
                                fieldWithPath("price").description("?????? ??????"),
                                fieldWithPath("trainers").description("?????? ???????????? ??????"),
                                fieldWithPath("images").description("?????? ????????? ??????"),
                                fieldWithPath("images[].local").description("????????? ??????"),
                                fieldWithPath("images[].path").description("????????? ??????"),
                                fieldWithPath("equipmentNumbers").description("?????? ??????"),
                                fieldWithPath("equipmentNumbers[].equipment").description("?????? ??????"),
                                fieldWithPath("equipmentNumbers[].number").description("?????? ???")

                        )
                ));

    }
}
