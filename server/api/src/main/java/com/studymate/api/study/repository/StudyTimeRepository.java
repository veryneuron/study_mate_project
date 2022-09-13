package com.studymate.api.study.repository;

import com.studymate.api.study.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyTimeRepository extends JpaRepository<StudyTime, Integer> {
}
