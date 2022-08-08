package com.studymate.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.user.dto.RegistrationDTO;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class RegistrationControllerTest {
    //Integration Test
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    private String accessToken;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        StudyUser studyUser = new StudyUser();
        studyUser.setUserId("test");
        studyUser.setNickname("testnick");
        studyUser.setUserPassword("testpassword");
        studyUser.setTemperatureSetting(27.27f);
        studyUser.setHumiditySetting(67.2f);
        studyUser.setRasberrypiAddress("123.456.789.102");
        authService.createUser(studyUser);
        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
        rabbitAdmin.purgeQueue("settingQueue");
    }

    @AfterEach
    void tearDown() {
        rabbitAdmin.purgeQueue("settingQueue");
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        mockMvc.perform(get("/api/registration")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test DTO validation")
    void DtoValidationTest() throws Exception {
        RegistrationDTO user = new RegistrationDTO();
        user.setHumiditySetting(1234.123456789f);
        user.setTemperatureSetting(12345.123456789f);
        user.setRasberrypiAddress("123.456.789.1011.1213.1415.1617.1819.2020.2222.2424.2626.2828.3030.3232.3434.3636.3838.4040.4242.4444.46");
        mockMvc.perform(put("/api/registration")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Temperature setting must be a number")))
                .andExpect(content().string(containsString("Humidity setting must be a number")))
                .andExpect(content().string(containsString("Maximum length of address is 45")))
                .andDo(print());
    }

    //getSettingValue

    @Test
    @DisplayName("test getSettingValue normal case")
    void getSettingValueTest() throws Exception {
        mockMvc.perform(get("/api/registration")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"temperatureSetting\":27.27,\"humiditySetting\":67.2,\"rasberrypiAddress\":\"123.456.789.102\"}"));
    }

    @Test
    @DisplayName("test getSettingValue error case")
    void getSettingValueErrorTest() throws Exception {
        String errorToken = jwtTokenProvider.createToken("test1");
        mockMvc.perform(get("/api/registration")
                        .header("Authorization", "Bearer " + errorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }

    //setSettingValue

    @Test
    @DisplayName("test setSettingValue normal case")
    void setSettingValueTest() throws Exception {
        RegistrationDTO user = new RegistrationDTO();
        user.setHumiditySetting(567.987f);
        user.setTemperatureSetting(874.231f);
        user.setRasberrypiAddress("000.000.000.000");
        mockMvc.perform(put("/api/registration")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Successfully set value")));
        await().atMost(3, TimeUnit.SECONDS).until(() -> rabbitTemplate.receive("settingQueue") != null);
    }

    @Test
    @DisplayName("test setSettingValue error case")
    void setSettingValueErrorTest() throws Exception {
        String errorToken = jwtTokenProvider.createToken("test1");
        RegistrationDTO user = new RegistrationDTO();
        user.setHumiditySetting(567.987f);
        user.setTemperatureSetting(874.231f);
        user.setRasberrypiAddress("000.000.000.000");
        mockMvc.perform(put("/api/registration")
                        .header("Authorization", "Bearer " + errorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to set value - Illegal Argument")));
    }

}