package com.studymate.api.measurement.controller;

import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MeasurementControllerTest {
    //Integration Test
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MeasurementDataRepository measurementDataRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private String accessToken;

    @BeforeEach
    void setUp() {
        measurementDataRepository.deleteAll();
        MeasurementData measurementData = MeasurementData.builder()
                .temperature(37.5f)
                .humidity(75.12f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.102")
                .userId("test")
                .build();
        MeasurementData measurementData2 = MeasurementData.builder()
                .temperature(25.6f)
                .humidity(82.10f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.102")
                .userId("test")
                .build();
        MeasurementData measurementData3 = MeasurementData.builder()
                .temperature(41.2f)
                .humidity(72.11f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.103")
                .userId("test10")
                .build();
        measurementDataRepository.save(measurementData);
        measurementDataRepository.save(measurementData2);
        measurementDataRepository.save(measurementData3);

        StudyUser studyUser = new StudyUser();
        studyUser.setUserId("test");
        studyUser.setNickname("testnick");
        studyUser.setUserPassword("testpassword");
        studyUser.setTemperatureSetting(27.27f);
        studyUser.setHumiditySetting(67.2f);
        studyUser.setRasberrypiAddress("123.456.789.102");
        authService.createUser(studyUser);
        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
    }
    @AfterEach
    void tearDown() {
        measurementDataRepository.deleteAll();
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        mockMvc.perform(get("/api/measurement")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("test retrieveMeasureData normal case")
    void retrieveMeasureData() throws Exception {
        mockMvc.perform(get("/api/measurement")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    @DisplayName("test retrieveMeasureData error case")
    void retriveMeasureDataErrorTest() throws Exception {
        String errorToken = jwtTokenProvider.createToken("test1");
        mockMvc.perform(get("/api/measurement")
                        .header("Authorization", "Bearer " + errorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }
}