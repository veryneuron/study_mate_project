package com.studymate.api.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.study.dto.UserStatus;
import com.studymate.api.study.dto.UserStatusDTO;
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
import org.springframework.util.LinkedMultiValueMap;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
    private StudyUserRepository studyUserRepository;
    @Autowired
    private StudyTimeRepository studyTimeRepository;
    @Autowired
    private StudyRecordRepository studyRecordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken;
    private StudyUser studyUser;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        studyUserRepository.deleteAll();
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
        System.out.println(org.hibernate.Version.getVersionString());
        studyUser = StudyUser.builder()
                .userId("test").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime1 = new StudyTime();
        studyTime1.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime1.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime1.setUserId("test");
        StudyTime studyTime2 = new StudyTime();
        studyTime2.setStartTimestamp(currentTime.minusHours(3).minusMinutes(50));
        studyTime2.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime2.setUserId("test");
        StudyTime savedStudyTime1 = studyTimeRepository.save(studyTime1);
        StudyTime savedStudyTime2 = studyTimeRepository.save(studyTime2);

        StudyRecord studyRecord1 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord1.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord2 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord2.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(10));
        StudyRecord studyRecord3 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord3.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(30));
        StudyRecord studyRecord4 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord4.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(10));

//        studyRecordRepository.saveAll(List.of(studyRecord1, studyRecord2, studyRecord3, studyRecord4));
        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        studyTime1.setEndTimestampWithTotalTime(currentTime.minusHours(1).minusMinutes(10));

        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        studyTime2.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(20));

//        studyTimeRepository.saveAll(List.of(studyTime1, studyTime2));
        savedUser.get().addStudyTime(studyTime1);
        savedUser.get().addStudyTime(studyTime2);
        studyUserRepository.save(savedUser.get());

        studyUser = savedUser.get();
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

    @Test
    @DisplayName("test checkUserStatus empty time case")
    void checkUserStatusEmptyTimeTest() throws Exception {
        StudyUser emptyUser = StudyUser.builder()
                .userId("empty").nickname("testnick3").userPassword("testpassword3").build();
        authService.createUser(emptyUser);

        accessToken = authService.authenticate(emptyUser.getUserId(), "testpassword3");
        mockMvc.perform(get("/api/study")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userIds", emptyUser.getUserId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(UserStatusDTO.builder()
                                .userStatus(List.of(new UserStatus(emptyUser.getUserId(), false, false)))
                                .build())));
    }

    @Test
    @DisplayName("test checkUserStatus normal case")
    void checkUserStatusNormalTest() throws Exception {
        StudyUser nowStudying = StudyUser.builder()
                .userId("test2").nickname("testnick2").userPassword("testpassword2").build();
        authService.createUser(nowStudying);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test2");
        StudyTime studyTime1 = new StudyTime();
        studyTime1.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime1.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime1.setUserId("test2");
        StudyTime savedStudyTime1 = studyTimeRepository.save(studyTime1);

        StudyRecord studyRecord1 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("test2")
                .build();
        studyRecord1.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord2 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("test2")
                .build();
        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        savedUser.get().addStudyTime(studyTime1);
        studyUserRepository.save(savedUser.get());

        StudyUser studyingNotRecording = StudyUser.builder()
                .userId("test3").nickname("testnick3").userPassword("testpassword3").build();
        authService.createUser(studyingNotRecording);
        Optional<StudyUser> savedNotRecordingUser = studyUserRepository.findByUserId("test3");
        StudyTime studyTime2 = new StudyTime();
        studyTime2.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime2.setUserSerialNumber(savedNotRecordingUser.get().getUserSerialNumber());
        studyTime2.setUserId("test3");
        StudyTime savedStudyTime2 = studyTimeRepository.save(studyTime2);

        StudyRecord studyRecord3 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("test3")
                .build();
        studyRecord3.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord4 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("test3")
                .build();
        studyRecord4.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(10));
        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        savedNotRecordingUser.get().addStudyTime(studyTime2);
        studyUserRepository.save(savedNotRecordingUser.get());

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userIds", studyUser.getUserId());
        params.add("userIds", nowStudying.getUserId());
        params.add("userIds", studyingNotRecording.getUserId());

        assertEquals(3, studyUserRepository.count());
        assertEquals(4, studyTimeRepository.count());
        assertEquals(8, studyRecordRepository.count());

        UserStatusDTO userStatusDTO = new UserStatusDTO(
                List.of(new UserStatus(studyUser.getUserId(), studyUser.isTiming(), studyUser.isRecording()),
                new UserStatus(savedUser.get().getUserId(), savedUser.get().isTiming(), savedUser.get().isRecording()),
                new UserStatus(savedNotRecordingUser.get().getUserId()
                        , savedNotRecordingUser.get().isTiming()
                        , savedNotRecordingUser.get().isRecording())));

        accessToken = authService.authenticate(nowStudying.getUserId(), "testpassword2");
        mockMvc.perform(get("/api/study")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(userStatusDTO)));
    }
}