package com.studymate.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.auth.dto.StudyUserDTO;
import com.studymate.api.auth.entity.StudyUser;
import com.studymate.api.auth.jwt.JwtTokenProvider;
import com.studymate.api.auth.service.AuthService;
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
    private final ObjectMapper mapper = new ObjectMapper();
    private String accessToken;
    private StudyUser studyUser;
    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        studyUser = new StudyUser();
        studyUser.setUserId("test");
        studyUser.setNickname("testnick");
        studyUser.setUserPassword("testpassword");
        authService.createUser(studyUser);
        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        StudyUserDTO user = new StudyUserDTO();
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
        StudyUserDTO user = new StudyUserDTO();
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
        StudyUserDTO user = new StudyUserDTO();
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
        StudyUserDTO user = new StudyUserDTO();
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
        StudyUserDTO user = modelMapper.map(studyUser, StudyUserDTO.class);
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
        StudyUserDTO user = new StudyUserDTO();
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
        StudyUserDTO user = modelMapper.map(studyUser, StudyUserDTO.class);
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
        StudyUserDTO user = modelMapper.map(newUser, StudyUserDTO.class);
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
        StudyUserDTO user = modelMapper.map(studyUser, StudyUserDTO.class);
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
        StudyUserDTO user = new StudyUserDTO();
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