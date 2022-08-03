package com.studymate.api.auth.repository;

import com.studymate.api.auth.entity.StudyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyUserRepository extends JpaRepository<StudyUser, Integer> {
    Optional<StudyUser> findStudyUserByUserId(String userId);
}
