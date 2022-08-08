package com.studymate.api.user.repository;

import com.studymate.api.user.entity.StudyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyUserRepository extends JpaRepository<StudyUser, Integer> {
    Optional<StudyUser> findByUserId(String userId);
}
