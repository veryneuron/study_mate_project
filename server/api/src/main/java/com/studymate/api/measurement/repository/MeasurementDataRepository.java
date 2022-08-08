package com.studymate.api.measurement.repository;

import com.studymate.api.measurement.model.MeasurementData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementDataRepository extends MongoRepository<MeasurementData, String> {
    List<MeasurementData> findByRaspberrypiAddress(String raspberrypiAddress);
}