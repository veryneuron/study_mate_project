package com.studymate.api.study.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "study_record")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StudyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_record_sn")
    private Integer studyRecordSerialNumber;
    @NotNull
    @Column(name = "study_time_sn")
    private Integer studyTimeSerialNumber;
    @NotNull
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @NotNull
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyRecord that = (StudyRecord) o;
        return studyRecordSerialNumber != null && Objects.equals(studyRecordSerialNumber, that.studyRecordSerialNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
