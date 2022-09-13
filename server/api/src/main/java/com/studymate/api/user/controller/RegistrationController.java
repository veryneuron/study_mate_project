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
    public ResponseEntity<RegistrationDTO> getSettingValue(@AuthenticationPrincipal String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        StudyUser studyUser = registrationService.findUser(userId);
        RegistrationDTO registrationDTO = modelMapper.map(studyUser, RegistrationDTO.class);
        return ResponseEntity.ok().body(registrationDTO);
    }

    @PutMapping
    public ResponseEntity<String> setSettingValue(@AuthenticationPrincipal String userId, @Valid @RequestBody RegistrationDTO registrationDTO) throws JsonProcessingException {
        if (userId == null || registrationDTO == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        StudyUser studyUser = modelMapper.map(registrationDTO, StudyUser.class);
        studyUser.setUserId(userId);
        registrationService.setValue(studyUser);
        return ResponseEntity.ok("Successfully set value");
    }
}
