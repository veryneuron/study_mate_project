package com.studymate.api.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.studymate.api.user.dto.RegistrationDTO;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public ResponseEntity<?> getSettingValue(@AuthenticationPrincipal String userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("Illegal argument");
            }
            StudyUser studyUser = registrationService.findUser(userId);
            RegistrationDTO registrationDTO = modelMapper.map(studyUser, RegistrationDTO.class);
            return ResponseEntity.ok().body(registrationDTO);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to get data - Illegal Argument");
        }
    }

    @PutMapping
    public ResponseEntity<?> setSettingValue(@AuthenticationPrincipal String userId, @Valid @RequestBody RegistrationDTO registrationDTO) {
        try {
            if (userId == null || registrationDTO == null) {
                throw new IllegalArgumentException("Illegal argument");
            }
            StudyUser studyUser = modelMapper.map(registrationDTO, StudyUser.class);
            studyUser.setUserId(userId);
            registrationService.setValue(studyUser);
            return ResponseEntity.ok("Successfully set value");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to set value - Illegal Argument");
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to set value - Json processing error");
        }
    }

}
