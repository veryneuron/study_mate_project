package com.studymate.api.user.controller;

import com.studymate.api.user.dto.AuthDTO;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthDTO authDTO) {
        try {
            StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
            authService.createUser(studyUser);
            return ResponseEntity.ok("Successfully signed up");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(authDTO);
        }
    }

    @GetMapping("/signin")
    public ResponseEntity<?> signing(@Valid @RequestBody AuthDTO authDTO) {
        try {
            StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
            String jwtToken = authService.authenticate(studyUser.getUserId(), studyUser.getUserPassword());
            return ResponseEntity.ok().body(jwtToken);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(authDTO);
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserData(@AuthenticationPrincipal String userId) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("Illegal argument");
            }
            StudyUser studyUser = authService.findUser(userId);
            AuthDTO authDTO = modelMapper.map(studyUser, AuthDTO.class);
            return ResponseEntity.ok().body(authDTO);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to get data - Illegal Argument");
        }
    }

    @PutMapping
    public ResponseEntity<?> editing(@Valid @RequestBody AuthDTO authDTO) {
        try {
            StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
            authService.editUser(studyUser);
            return ResponseEntity.ok("Successfully edited");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to edit - Illegal Argument");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleting(@Valid @RequestBody AuthDTO authDTO) {
        try {
            StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
            authService.deleteUser(studyUser.getUserId());
            return ResponseEntity.ok("Successfully deleted");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete - Illegal Argument");
        }
    }

}
