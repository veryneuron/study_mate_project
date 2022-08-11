package com.studymate.api.measurement.controller;

import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/measurement")
@RequiredArgsConstructor
public class MeasurementController {
    private final MeasurementDataRepository measurementDataRepository;

    @GetMapping
    public ResponseEntity<?> retrieveMeasureData(@AuthenticationPrincipal String userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("Illegal argument");
            }
            List<MeasurementData> data = measurementDataRepository.findByUserIdOrderByTimestampDesc(userId);
            if (data.size() > 0) {
                return ResponseEntity.ok(data);
            } else {
                throw new IllegalArgumentException("No data exists");
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to get data - Illegal Argument");
        }
    }
}
