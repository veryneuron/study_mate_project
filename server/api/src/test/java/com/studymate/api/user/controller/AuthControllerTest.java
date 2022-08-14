package com.studymate.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.study.repository.StudyRecordRepository;
import com.studymate.api.study.repository.StudyTimeRepository;
import com.studymate.api.user.dto.AuthDTO;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.repository.StudyUserRepository;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    //Integration Test
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    StudyUserRepository studyUserRepository;
    @Autowired
    StudyTimeRepository studyTimeRepository;
    @Autowired
    StudyRecordRepository studyRecordRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private String accessToken;
    private StudyUser studyUser;
    private final ModelMapper modelMapper = new ModelMapper();
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        studyUserRepository.deleteAll();
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
        studyUser = StudyUser.builder()
                .userId("test").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime1 = StudyTime.builder()
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userSerialNumber(savedUser.get().getUserSerialNumber())
                .userId(savedUser.get().getUserId())
                .build();
        StudyTime studyTime2 = StudyTime.builder()
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userSerialNumber(savedUser.get().getUserSerialNumber())
                .userId(savedUser.get().getUserId())
                .build();
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

        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        studyTime1.setEndTimestampWithTotalTime(currentTime.minusHours(1).minusMinutes(10));

        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        studyTime2.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(20));

        savedUser.get().addStudyTime(studyTime1);
        savedUser.get().addStudyTime(studyTime2);
        studyUser = studyUserRepository.save(savedUser.get());

        assertEquals(2, studyTimeRepository.count());
        assertEquals(4, studyRecordRepository.count());
        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setUserId("test");
        user.setNickname("updatednick");
        user.setUserPassword("updatedpw");
        mockMvc.perform(put("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("test DTO validation")
    void DtoValidationTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setUserId("012345678901234567890123456789");
        user.setNickname("012345678901234567890123456789");
        user.setUserPassword("012345678901234567890123456789");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Maximum length of userId is 20")))
                .andExpect(content().string(containsString("Maximum length of nickname is 20")))
                .andExpect(content().string(containsString("Maximum length of userPassword is 20")))
                .andDo(print());
    }

    @Test
    @DisplayName("test signup normal case")
    void signupTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setUserId("newtest");
        user.setNickname("newnick");
        user.setUserPassword("newpw");
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Successfully signed up")));
        assertNotNull(authService.findUser("newtest"));
    }

    @Test
    @DisplayName("test signup error case")
    void signupErrorTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setUserId("test");
        user.setNickname("newnick");
        user.setUserPassword("newpw");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test signin normal case")
    void signinTest() throws Exception {
        AuthDTO user = modelMapper.map(studyUser, AuthDTO.class);
        user.setUserPassword("testpassword");
        MvcResult result = mockMvc.perform(get("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(jwtTokenProvider.getAuthentication(token).getPrincipal(), studyUser.getUserId());
    }

    @Test
    @DisplayName("test signin error case")
    void signinErrorTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setUserId("test1");
        user.setNickname("newnick");
        user.setUserPassword("newpw");
        mockMvc.perform(get("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test findUserData normal case")
    void getUserDataTest() throws Exception {
        mockMvc.perform(get("/api/auth")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test")));
    }

    @Test
    @DisplayName("test findUserData error case")
    void getUserDataErrorTest() throws Exception {
        String errorToken = jwtTokenProvider.createToken("test1");
        mockMvc.perform(get("/api/auth")
                        .header("Authorization", "Bearer " + errorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }

    @Test
    @DisplayName("test editing normal case")
    void editTest() throws Exception {
        studyUser.setNickname("updatednick");
        studyUser.setUserPassword("updatedpw");
        AuthDTO user = modelMapper.map(studyUser, AuthDTO.class);
        mockMvc.perform(put("/api/auth")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Successfully edited")));
        assertEquals(authService.findUser("test").getNickname(), "updatednick");
    }

    @Test
    @DisplayName("test editing error case")
    void editErrorTest() throws Exception {
        StudyUser newUser = new StudyUser();
        newUser.setUserId("anothertest");
        newUser.setNickname("anothernick");
        newUser.setUserPassword("anotherpw");
        AuthDTO user = modelMapper.map(newUser, AuthDTO.class);
        mockMvc.perform(put("/api/auth")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to edit - Illegal Argument")));
    }

    @Test
    @DisplayName("test deleting normal case")
    void deletingTest() throws Exception {
        AuthDTO user = modelMapper.map(studyUser, AuthDTO.class);
        user.setUserPassword("testpassword");
        mockMvc.perform(delete("/api/auth")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Successfully deleted")));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.findUser(user.getUserId()));
        assertEquals("UserId does not exist", error.getMessage());
    }

    @Test
    @DisplayName("test deleting error case")
    void deletingErrorTest() throws Exception {
        AuthDTO user = new AuthDTO();
        user.setNickname("testnick");
        user.setUserPassword("testpassword");
        mockMvc.perform(delete("/api/auth")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to delete - Illegal Argument")));
    }
}