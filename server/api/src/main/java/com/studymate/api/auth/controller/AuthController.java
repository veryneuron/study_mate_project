package com.studymate.api.auth.controller;

import com.studymate.api.auth.dto.StudyUserDTO;
import com.studymate.api.auth.entity.StudyUser;
import com.studymate.api.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<?> signup(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            authService.createUser(studyUser);
            return ResponseEntity.ok("Successfully signed up");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(studyUserDTO);
        }
    }

    @GetMapping
    public ResponseEntity<?> signing(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            String jwtToken = authService.authenticate(studyUser.getUserId(), studyUser.getUserPassword());
            return ResponseEntity.ok().body(jwtToken);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(studyUserDTO);
        }
    }

    @PutMapping
    public ResponseEntity<?> editing(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            authService.editUser(studyUser);
            return ResponseEntity.ok("Successfully edited");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to edit - Illegal Argument");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleting(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            authService.deleteUser(studyUser.getUserId());
            return ResponseEntity.ok("Successfully deleted");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete - Illegal Argument");
        }
    }

}
