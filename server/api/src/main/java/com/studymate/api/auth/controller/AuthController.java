package com.studymate.api.auth.controller;

import com.studymate.api.auth.dto.StudyUserDTO;
import com.studymate.api.auth.entity.StudyUser;
import com.studymate.api.auth.jwt.JwtTokenProvider;
import com.studymate.api.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            StudyUser registeredUser = authService.createUser(studyUser);
            StudyUserDTO responseUser = modelMapper.map(registeredUser, StudyUserDTO.class);
            return ResponseEntity.ok().body(responseUser);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(studyUserDTO);
        }
    }

    @GetMapping("/signing")
    public ResponseEntity<?> signing(@Valid @RequestBody StudyUserDTO studyUserDTO) {
        try {
            StudyUser studyUser = modelMapper.map(studyUserDTO, StudyUser.class);
            Optional<StudyUser> signedUser = authService.authenticate(studyUser.getUserId());
            if (signedUser.isPresent()) {
                final String jwtToken = jwtTokenProvider.createToken(signedUser.get().getUserId());
                return ResponseEntity.ok().body(jwtToken);
            } else {
                log.info("UserId already exists");
                return ResponseEntity.badRequest().body(studyUserDTO);
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(studyUserDTO);
        }
    }

}
