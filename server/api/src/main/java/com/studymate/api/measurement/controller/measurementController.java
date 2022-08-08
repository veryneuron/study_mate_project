package com.studymate.api.measurement.controller;

import com.studymate.api.measurement.repository.MeasurementDataRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/measurement")
@RequiredArgsConstructor
public class measurementController {
    private final MeasurementDataRepository measurementDataRepository;
    private final StudyUserRepository studyUserRepository;

    @GetMapping
    public ResponseEntity<?> retrieveMeasureData(@AuthenticationPrincipal String userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("Illegal argument");
            }
            Optional<StudyUser> user = studyUserRepository.findByUserId(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(measurementDataRepository.findByRaspberrypiAddress(user.get().getRasberrypiAddress()));
            } else {
                throw new IllegalArgumentException("UserId is not found");
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to get data - Illegal Argument");
        }
    }
}
