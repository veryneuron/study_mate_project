package com.studymate.api.user.service;

import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.repository.StudyUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    AuthService authService;
    @Mock
    StudyUserRepository studyUserRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    StudyUser studyUser;
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    @BeforeEach
    void setUp() {
        studyUser = new StudyUser();
        studyUser.setUserId("test");
        studyUser.setNickname("testnickname");
        String password = passwordEncoder.encode("testpassword");
        studyUser.setUserPassword(password);

        authService = new AuthService(studyUserRepository, jwtTokenProvider, passwordEncoder);
        jwtTokenProvider.setSecretKey("secretkeyfortestpurpose1234567890");
        jwtTokenProvider.init();
        assertNotNull(studyUserRepository);
    }

    //createUser

    @Test
    @DisplayName("test createUser normal case")
    void testNormalCreateUser() {
        StudyUser newUser = new StudyUser();
        newUser.setUserId("test1");
        newUser.setNickname("testnick1");
        newUser.setUserPassword("testpassword1");
        when(studyUserRepository.findStudyUserByUserId("test1")).thenReturn(Optional.empty());
        when(studyUserRepository.save(newUser)).thenReturn(newUser);
        StudyUser createdUser = authService.createUser(newUser);
        verify(studyUserRepository, times(1)).save(newUser);
        assertAll("editedUser",
                () -> assertEquals(newUser.getUserId(), createdUser.getUserId()),
                () -> assertEquals(newUser.getNickname(), createdUser.getNickname()),
                () -> assertTrue(passwordEncoder.matches("testpassword1", createdUser.getUserPassword()))
        );
    }

    @Test
    @DisplayName("test createUser existing case")
    void testExistingCreateUser() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.createUser(studyUser));
        assertEquals("UserId already exists", error.getMessage());
    }

    @Test
    @DisplayName("test createUser null argument case")
    void testNullArgumentCreateUser() {
        StudyUser newUser = new StudyUser();
        newUser.setUserId("test1");
        newUser.setUserPassword("testpassword1");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.createUser(newUser));
        assertEquals("Please check arguments", error.getMessage());
    }

    //authenticate

    @Test
    @DisplayName("test authenticate normal case")
    void testAuthenticate() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        String token = authService.authenticate("test", "testpassword");
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(jwtTokenProvider.getAuthentication(token).getPrincipal(), studyUser.getUserId());
    }

    @Test
    @DisplayName("test authenticate null argument case")
    void testAuthenticateNullArgument() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null, null));
        assertEquals("Please check arguments", error.getMessage());
    }

    @Test
    @DisplayName("test authenticate empty user case")
    void testAuthenticateEmptyUser() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.authenticate("emptyUser", null));
        assertEquals("UserId does not exist", error.getMessage());
    }

    @Test
    @DisplayName("test authenticate wrong password case")
    void testAuthenticateWrongPassword() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.authenticate("test", "wrongpassword"));
        assertEquals("Wrong password", error.getMessage());
    }

    //editUser

    @Test
    @DisplayName("test editUser normal case")
    void testNormalEditUser() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        when(studyUserRepository.save(any())).then(i -> i.getArgument(0, StudyUser.class));
        StudyUser editUser = new StudyUser();
        editUser.setUserId("test");
        editUser.setNickname("editednickname");
        editUser.setUserPassword("editedpassword");
        StudyUser editedUser = authService.editUser(editUser);
        verify(studyUserRepository, times(1)).save(editUser);
        assertAll("editedUser",
                () -> assertEquals(editUser.getUserId(), editedUser.getUserId()),
                () -> assertEquals(editUser.getNickname(), editedUser.getNickname()),
                () -> assertTrue(passwordEncoder.matches("editedpassword", editedUser.getUserPassword()))
        );
    }

    @Test
    @DisplayName("test editUser not existing case")
    void testNotExistingEditUser() {
        when(studyUserRepository.findStudyUserByUserId("test1")).thenReturn(Optional.empty());
        StudyUser studyUser = new StudyUser();
        studyUser.setUserId("test1");
        studyUser.setNickname("testnickname");
        studyUser.setUserPassword(passwordEncoder.encode("testpassword1"));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.editUser(studyUser));
        assertEquals("UserId does not exist", error.getMessage());
    }

    @Test
    @DisplayName("test editUser null argument case")
    void testNullArgumentEditUser() {
        StudyUser newUser = new StudyUser();
        newUser.setNickname("testnickname");
        newUser.setUserPassword(passwordEncoder.encode("testpassword1"));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.editUser(newUser));
        assertEquals("Please check arguments", error.getMessage());
    }

    //deleteUser

    @Test
    @DisplayName("test deleteUser normal case")
    void testNormalDeleteUser() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        doNothing().when(studyUserRepository).delete(studyUser);
        authService.deleteUser(studyUser.getUserId());
        verify(studyUserRepository, times(1)).delete(studyUser);
    }

    @Test
    @DisplayName("test deleteUser not existing case")
    void testNotExistingDeleteUser() {
        when(studyUserRepository.findStudyUserByUserId("notexisting")).thenReturn(Optional.empty());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.deleteUser("notexisting"));
        assertEquals("UserId does not exist", error.getMessage());
    }

    @Test
    @DisplayName("test deleteUser null argument case")
    void testNullArgumentDeleteUser() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.editUser(null));
        assertEquals("Please check arguments", error.getMessage());
    }

    //getUser

    @Test
    @DisplayName("test getUser normal case")
    void testNormalGetUser() {
        when(studyUserRepository.findStudyUserByUserId("test")).thenReturn(Optional.of(studyUser));
        StudyUser user = authService.findUser("test");
        assertAll("editedUser",
                () -> assertEquals(studyUser.getUserId(), user.getUserId()),
                () -> assertEquals(studyUser.getNickname(), user.getNickname()),
                () -> assertTrue(passwordEncoder.matches("testpassword", user.getUserPassword()))
        );
    }

    @Test
    @DisplayName("test getUser not existing case")
    void testNotExistingGetUser() {
        when(studyUserRepository.findStudyUserByUserId("test1")).thenReturn(Optional.empty());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.findUser("test1"));
        assertEquals("UserId does not exist", error.getMessage());
    }

}