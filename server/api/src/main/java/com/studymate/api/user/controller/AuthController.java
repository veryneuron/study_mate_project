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
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<String> signup(@Valid @RequestBody AuthDTO authDTO) {
        StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
        authService.createUser(studyUser);
        return ResponseEntity.ok("Successfully signed up");
    }

    @PostMapping("/token")
    public ResponseEntity<String> signing(@Valid @RequestBody AuthDTO authDTO) {
        StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
        String jwtToken = authService.authenticate(studyUser.getUserId(), studyUser.getUserPassword());
        return ResponseEntity.ok().body(jwtToken);
    }

    @GetMapping
    public ResponseEntity<AuthDTO> getUserData(@AuthenticationPrincipal String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        StudyUser studyUser = authService.findUser(userId);
        AuthDTO authDTO = modelMapper.map(studyUser, AuthDTO.class);
        return ResponseEntity.ok().body(authDTO);
    }

    @PutMapping
    public ResponseEntity<String> editing(@Valid @RequestBody AuthDTO authDTO) {
        StudyUser studyUser = modelMapper.map(authDTO, StudyUser.class);
        authService.editUser(studyUser);
        return ResponseEntity.ok("Successfully edited");
    }

    @DeleteMapping
    public ResponseEntity<String> deleting(@AuthenticationPrincipal String userId) {
        authService.deleteUser(userId);
        return ResponseEntity.ok("Successfully deleted");
    }

}
