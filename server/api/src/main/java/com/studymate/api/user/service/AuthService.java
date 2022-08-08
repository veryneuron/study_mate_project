package com.studymate.api.user.service;

import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.repository.StudyUserRepository;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public StudyUser createUser(final StudyUser studyUser) {
        if (studyUser == null || studyUser.getUserId() == null
        || studyUser.getNickname() == null || studyUser.getUserPassword() == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("Please check arguments");
        }
        if (studyUserRepository.findByUserId(studyUser.getUserId()).isPresent()) {
            log.info("UserId already exists");
            throw new IllegalArgumentException("UserId already exists");
        }
        studyUser.setUserPassword(passwordEncoder.encode(studyUser.getUserPassword()));
        return studyUserRepository.save(studyUser);
    }

    public String authenticate(final String userId, String userPassword) {
        if (userId == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("Please check arguments");
        }
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        if (passwordEncoder.matches(userPassword, studyUser.get().getUserPassword())) {
            return jwtTokenProvider.createToken(studyUser.get().getUserId());
        } else {
            log.info("Wrong password");
            throw new IllegalArgumentException("Wrong password");
        }
    }

    public StudyUser findUser(final String userId) {
        Optional<StudyUser> userResult = studyUserRepository.findByUserId(userId);
        if (userResult.isPresent()) {
            return userResult.get();
        } else {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
    }

    public StudyUser editUser(final StudyUser studyUser) {
        if (studyUser == null || studyUser.getUserId() == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("Please check arguments");
        }
        Optional<StudyUser> editedUser = studyUserRepository.findByUserId(studyUser.getUserId());
        if (editedUser.isPresent()) {
            if (studyUser.getNickname() != null) {
                editedUser.get().setNickname(studyUser.getNickname());
            }
            if (studyUser.getUserPassword() != null) {
                editedUser.get().setUserPassword(passwordEncoder.encode(studyUser.getUserPassword()));
            }
            return studyUserRepository.save(editedUser.get());
        } else {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
    }

    public void deleteUser(final String userId) {
        if (userId == null) {
            log.warn("Illegal argument");
            throw new IllegalArgumentException("Please check arguments");
        }
        Optional<StudyUser> deletedUser = studyUserRepository.findByUserId(userId);
        if (deletedUser.isPresent()) {
            studyUserRepository.delete(deletedUser.get());
        } else {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
    }
}
