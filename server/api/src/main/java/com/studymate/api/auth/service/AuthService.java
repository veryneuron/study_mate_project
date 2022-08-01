package com.studymate.api.auth.service;

import com.studymate.api.auth.entity.StudyUser;
import com.studymate.api.auth.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudyUserRepository studyUserRepository;
    private final PasswordEncoder passwordEncoder;

    public StudyUser createUser(final StudyUser studyUser) {
        if (studyUser == null || studyUser.getUserId() == null
        || studyUser.getNickname() == null || studyUser.getUserPassword() == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("Please check arguments");
        }
        if (studyUserRepository.findStudyUserByUserId(studyUser.getUserId()).isPresent()) {
            log.info("Cannot save user - existing user");
            throw new IllegalArgumentException("UserId already exists");
        }
        studyUser.setUserPassword(passwordEncoder.encode(studyUser.getUserPassword()));
        return studyUserRepository.save(studyUser);
    }

    public Optional<StudyUser> authenticate(final String userId, String userPassword) {
        if (userId == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("check arguments");
        }
        userPassword = passwordEncoder.encode(userPassword);
        return studyUserRepository.findStudyUserByUserIdAndUserPassword(userId, userPassword);
    }
}
