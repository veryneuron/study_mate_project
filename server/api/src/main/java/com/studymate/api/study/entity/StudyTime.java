package com.studymate.api.study.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "study_time")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StudyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_time_sn")
    private Integer studyTimeSerialNumber;
    @NotNull
    @Column(name = "user_sn")
    private Integer userSerialNumber;
    @NotNull
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @Nullable
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "study_time_sn")
    @ToString.Exclude
    private List<StudyRecord> studyRecords;
    @Nullable
    @Column(name = "total_time", columnDefinition = "interval")
    private Duration totalTime;
    @Nullable
    @Column(name = "focus_time", columnDefinition = "interval")
    private Duration focusTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyTime studyTime = (StudyTime) o;
        return studyTimeSerialNumber != null && Objects.equals(studyTimeSerialNumber, studyTime.studyTimeSerialNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Duration getCalculatedFocusTime() {
        if (focusTime == null) {
            return Duration.between(startTimestamp, LocalDateTime.now());
        } else {
            if (endTimestamp == null && studyRecords.get(studyRecords.size() - 1).getRecordTime() == null) {
                return focusTime.plus(studyRecords.get(studyRecords.size() - 1).getCalculatedRecordTime());
            } else {
                return focusTime;
            }
        }
    }

    public Duration getCalculatedNonFocusTime() {
        if (totalTime == null) {
            return Duration.between(startTimestamp, LocalDateTime.now());
        } else {
            return totalTime;
        }
    }

    public void addStudyRecordWithFocusTime(StudyRecord studyRecord) {
        if (studyRecords == null) {
            studyRecords = new ArrayList<>();
        }
        if (studyRecord != null) {
            if (focusTime == null && endTimestamp == null) {
                studyRecords.add(studyRecord);
                focusTime = studyRecord.getRecordTime();
            } else if (focusTime != null){
                if (studyRecords.get(studyRecords.size() - 1).getStartTimestamp()
                        .isAfter(studyRecord.getStartTimestamp())) {
                    throw new IllegalArgumentException("StartTimestamp is less than previous startTimestamp");
                }
                studyRecords.add(studyRecord);
                if (studyRecord.getRecordTime() != null) {
                    focusTime = focusTime.plus(studyRecord.getRecordTime());
                }
            }
        }
    }

    public void setEndTimestampWithTotalTime(LocalDateTime inputEndTimestamp) {
        if (inputEndTimestamp != null) {
            if (startTimestamp.isAfter(inputEndTimestamp)) {
                throw new IllegalArgumentException("EndTimestamp is less than startTimestamp");
            }
            endTimestamp = inputEndTimestamp;
            totalTime = Duration.between(startTimestamp, endTimestamp);
        }
    }

    public void updateLatestStudyRecord(LocalDateTime inputLatestEndTimestamp) {
        if (inputLatestEndTimestamp == null) {
            throw new IllegalArgumentException("LatestEndTimestamp is null");
        }
        if (studyRecords != null && studyRecords.size() > 0) {
            StudyRecord latestStudyRecord = studyRecords.get(studyRecords.size() - 1);
            if (latestStudyRecord != null) {
                latestStudyRecord.setEndTimestampWithRecordTime(inputLatestEndTimestamp);
                if (latestStudyRecord.getRecordTime() != null && focusTime != null) {
                    focusTime = focusTime.plus(latestStudyRecord.getRecordTime());
                }
            }
        } else {
            throw new IllegalStateException("StudyRecords are null or empty");
        }
    }
}
