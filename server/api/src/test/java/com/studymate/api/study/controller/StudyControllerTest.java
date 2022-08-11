package com.studymate.api.study.controller;

import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.study.repository.StudyRecordRepository;
import com.studymate.api.study.repository.StudyTimeRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.repository.StudyUserRepository;
import com.studymate.api.user.service.AuthService;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class StudyControllerTest {
    //Integration Test
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    StudyUserRepository studyUserRepository;
    @Autowired
    StudyTimeRepository studyTimeRepository;
    @Autowired
    StudyRecordRepository studyRecordRepository;
    private String accessToken;
    private StudyUser studyUser;

    @BeforeEach
    void setUp() {
        System.out.println(org.hibernate.Version.getVersionString());
        studyUser = StudyUser.builder()
                .userId("test").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime1 = new StudyTime();
        studyTime1.setStartTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50));
        studyTime1.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime1.setUserId("test");
        StudyTime studyTime2 = new StudyTime();
        studyTime2.setStartTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(50));
        studyTime2.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime2.setUserId("test");
        StudyTime savedStudyTime1 = studyTimeRepository.save(studyTime1);
        StudyTime savedStudyTime2 = studyTimeRepository.save(studyTime2);

        StudyRecord studyRecord1 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord1.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(30));
        StudyRecord studyRecord2 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord2.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(10));
        StudyRecord studyRecord3 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord3.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(3).minusMinutes(30));
        StudyRecord studyRecord4 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord4.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(3).minusMinutes(10));

//        studyRecordRepository.saveAll(List.of(studyRecord1, studyRecord2, studyRecord3, studyRecord4));
        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        studyTime1.setEndTimestampWithTotalTime(LocalDateTime.now().minusHours(1).minusMinutes(10));

        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        studyTime2.setEndTimestampWithTotalTime(LocalDateTime.now().minusHours(3).minusMinutes(20));

//        studyTimeRepository.saveAll(List.of(studyTime1, studyTime2));
        savedUser.get().addStudyTime(studyTime1);
        savedUser.get().addStudyTime(studyTime2);
        studyUserRepository.save(savedUser.get());

        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
        assertTrue(jwtTokenProvider.validateToken(accessToken));
        assertEquals(2, studyTimeRepository.count());
        assertEquals(4, studyRecordRepository.count());
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        mockMvc.perform(get("/api/study/current/focus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test retrieveCurrentFocus parameter error case")
    void retrieveStudyTimeCurrentFocusParameterTest() throws Exception {
        mockMvc.perform(get("/api/study/current/focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test retrieveCurrentFocus path error case")
    void retrieveStudyTimeCurrentFocusPathTest() throws Exception {
        mockMvc.perform(get("/api/study/error/error")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", studyUser.getUserId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus empty resultTime case")
    void retrieveStudyTimeCurrentFocusEmptyTimeTest() throws Exception {
        StudyUser emptyUser = StudyUser.builder()
                .userId("emptytest").nickname("emptynick").userPassword("emptypassword").build();
        authService.createUser(emptyUser);
        mockMvc.perform(get("/api/study/current/focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", emptyUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(Duration.ZERO.toString())));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus not existing user case")
    void retrieveStudyTimeCurrentFocusNotExistingUserTest() throws Exception {
        mockMvc.perform(get("/api/study/current/focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "notexistinguser"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus current/focus case")
    void retrieveStudyTimeCurrentFocusTest() throws Exception {
        mockMvc.perform(get("/api/study/current/focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", studyUser.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(studyUser
                                .getLatestStudyTime().get()
                                .getCalculatedFocusTime().toString())));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus current/non-focus case")
    void retrieveStudyTimeCurrentNonFocusTest() throws Exception {
        mockMvc.perform(get("/api/study/current/non-focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", studyUser.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(studyUser
                                .getLatestStudyTime().get()
                                .getCalculatedNonFocusTime().toString())));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus total/focus case")
    void retrieveStudyTimeTotalFocusTest() throws Exception {
        mockMvc.perform(get("/api/study/total/focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", studyUser.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(studyUser.getTotalFocusTime().toString())));
    }

    @Test
    @DisplayName("test retrieveCurrentFocus total/non-focus case")
    void retrieveStudyTimeTotalNonFocusTest() throws Exception {
        mockMvc.perform(get("/api/study/total/non-focus")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", studyUser.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(containsString(studyUser.getTotalStudyTime().toString())));
    }

}